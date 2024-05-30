package com.beeSpring.beespring.service.bid;

import com.beeSpring.beespring.domain.bid.Bid;
import com.beeSpring.beespring.domain.bid.Product;
import com.beeSpring.beespring.domain.user.User;
import com.beeSpring.beespring.dto.bid.BidDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface BidLogService {
    @Transactional
    Bid placeBid(Product product, User user, int price);

    @Transactional(readOnly = true)
    List<Bid> getBidsForProduct(Product product);

    /**
     * 마이페이지->입찰목록
     * @param serialNumber
     * @return
     */
    List<BidDTO> getMostRecentBidsByUser(String serialNumber);
}
