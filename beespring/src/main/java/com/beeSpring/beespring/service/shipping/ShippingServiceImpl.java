package com.beeSpring.beespring.service.shipping;

import com.beeSpring.beespring.domain.shipping.ShippingAddress;
import com.beeSpring.beespring.dto.shipping.ShippingAddressDTO;
import com.beeSpring.beespring.repository.shipping.ShippingAddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ShippingServiceImpl implements ShippingService{

    @Autowired
    private ShippingAddressRepository shippingAddressRepository;

    public List<ShippingAddress> getAllAddresses() {
        return shippingAddressRepository.findAll();
    }

    @Override
    public Optional<ShippingAddress> getAddressById(Long id) {
        return shippingAddressRepository.findById(id);
    }

    @Override
    public ShippingAddress saveAddress(ShippingAddressDTO addressDTO) {
        ShippingAddress address = new ShippingAddress(
                addressDTO.getAddressId(),
                addressDTO.getAddressName(),
                addressDTO.getDetailAddress(),
                addressDTO.getPostCode(),
                addressDTO.getRecipientName(),
                addressDTO.getRecipientPhone(),
                addressDTO.getRoadAddress(),
                addressDTO.getSerialNumber()
        );
        return shippingAddressRepository.save(address);
    }

    @Override
    public void deleteAddress(Long id) {
        shippingAddressRepository.deleteById(id);
    }

    @Override
    public List<ShippingAddress> getAddressesBySerialNumber(Long serialNumber) {
        return shippingAddressRepository.findBySerialNumber(serialNumber);
    }
}
