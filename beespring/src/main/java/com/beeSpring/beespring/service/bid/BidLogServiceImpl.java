package com.beeSpring.beespring.service.bid;

import com.beeSpring.beespring.controller.user.OAuth2Controller;
import com.beeSpring.beespring.domain.bid.Bid;
import com.beeSpring.beespring.domain.bid.Product;
import com.beeSpring.beespring.domain.user.User;
import com.beeSpring.beespring.dto.bid.BidDTO;
import com.beeSpring.beespring.repository.bid.BidLogRepository;
import com.beeSpring.beespring.repository.bid.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Service
public class BidLogServiceImpl implements BidLogService {
    private static final Logger logger = LoggerFactory.getLogger(BidLogServiceImpl.class);

    private final BidLogRepository bidLogRepository;
    private final ExecutorService executorService;
    private final ProductRepository productRepository;


    public BidLogServiceImpl(BidLogRepository bidLogRepository, ProductRepository productRepository) {
        this.bidLogRepository = bidLogRepository;
        this.productRepository = productRepository;
        this.executorService = Executors.newVirtualThreadPerTaskExecutor();
    }




    @Override
    @Transactional
    public Bid placeBid(Product product, User user, int price) {
        return CompletableFuture.supplyAsync(() -> {
            Integer maxPrice = bidLogRepository.findMaxPriceByProduct(product);
            if (maxPrice != null && price <= maxPrice) {
                throw new IllegalArgumentException("입찰 가격은 현재 입찰 최고가보다 높아야 합니다.");
            }
            // Product의 가격 업데이트
            productRepository.updateProductPrice(product.getProductId(), price);

            Bid bid = new Bid(product, user, price); // 입찰 객체 생성
            Bid savedBid = bidLogRepository.save(bid); // 입찰 객체를 데이터베이스에 저장

            return savedBid; // 입찰 객체를 데이터베이스에 저장
        }, executorService).join(); // 가상 스레드를 사용하여 비동기 작업을 실행하고, 결과를 동기적으로 기다림
    }

    @Override
    @Transactional(readOnly = true)
    public List<Bid> getBidsForProduct(Product product) {
        return CompletableFuture.supplyAsync(() -> bidLogRepository.findByProduct(product), executorService).join();
    }

    /**
     * 마이페이지->입찰 목록
     * @param serialNumber
     * @return
     */
    @Override
    public List<BidDTO> getMostRecentBidsByUser(String serialNumber) {
        List<Object[]> results = bidLogRepository.findMostRecentBidsByUser(serialNumber);
        List<BidDTO> bidDTOs = results.stream()
                .map(result -> {
                    Bid bid = (Bid) result[0];
                    String sellerNickname = (String) result[1];

                    BidDTO bidDTO = new BidDTO();
                    bidDTO.setProductId(bid.getProduct().getProductId());
                    bidDTO.setSellerId(bid.getProduct().getUser().getUserId());
                    bidDTO.setProductName(bid.getProduct().getProductName());
                    bidDTO.setProductInfo(bid.getProduct().getProductInfo());
                    bidDTO.setPrice(bid.getPrice());
                    bidDTO.setImage1(bid.getProduct().getImage1());
                    bidDTO.setDeadline(bid.getProduct().getDeadline());
                    bidDTO.setCurrentPrice(bid.getProduct().getPrice());
                    bidDTO.setNickName(sellerNickname); // Use seller nickname

                    // Log the BidDTO
                    logger.info("BidDTO: {}", bidDTO);

                    return bidDTO;
                })
                .collect(Collectors.toList());

        return bidDTOs;
    }


}
