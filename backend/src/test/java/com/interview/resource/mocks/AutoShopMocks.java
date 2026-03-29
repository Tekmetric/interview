package com.interview.resource.mocks;

import com.interview.resource.dto.AutoShopRequest;
import com.interview.resource.dto.AutoShopResponse;
import com.interview.resource.entity.AutoShop;

import java.util.List;

public class AutoShopMocks {

    public List<AutoShop> getAutoShops() {
        AutoShop shop1 = new AutoShop();
        shop1.setName("Discount Tires");
        shop1.setAddress("12989 Big Avenue");
        shop1.setCity("Memphis");
        shop1.setState("Tennessee");
        shop1.setContractYears(3);

        AutoShop shop2 = new AutoShop();
        shop2.setName("Big Apple Mechanics");
        shop2.setAddress("238 Big Apple Lane");
        shop2.setCity("New York");
        shop2.setState("New York");
        shop2.setContractYears(2);

        return List.of(
                shop1,
                shop2
        );
    }

    public List<AutoShopResponse> getAutoShopsResponses() {
        AutoShopResponse shop1 = new AutoShopResponse();
        shop1.setName("Discount Tires");
        shop1.setAddress("12989 Big Avenue");
        shop1.setCity("Memphis");
        shop1.setState("Tennessee");
        shop1.setContractYears(3);

        AutoShopResponse shop2 = new AutoShopResponse();
        shop2.setName("Big Apple Mechanics");
        shop2.setAddress("238 Big Apple Lane");
        shop2.setCity("New York");
        shop2.setState("New York");
        shop2.setContractYears(2);

        return List.of(
                shop1,
                shop2
        );
    }

    public AutoShop createShop() {
        AutoShop shop1 = new AutoShop();
        shop1.setName("Long Star Mechanics");
        shop1.setAddress("456 Pryor Rd NE");
        shop1.setCity("Houston");
        shop1.setState("Texas");
        shop1.setContractYears(5);

        return shop1;
    }

    public AutoShopResponse createShopResponse() {
        AutoShopResponse shop1 = new AutoShopResponse();
        shop1.setName("Long Star Mechanics");
        shop1.setAddress("456 Pryor Rd NE");
        shop1.setCity("Houston");
        shop1.setState("Texas");
        shop1.setContractYears(5);

        return shop1;
    }

    public AutoShopRequest createShopRequest() {
        AutoShopRequest shop1 = new AutoShopRequest();
        shop1.setName("Long Star Mechanics");
        shop1.setAddress("456 Pryor Rd NE");
        shop1.setCity("Houston");
        shop1.setState("Texas");
        shop1.setContractYears(5);

        return shop1;
    }

    public AutoShopRequest createShopRequest2() {
        AutoShopRequest shop1 = new AutoShopRequest();
        shop1.setName("New Star Mechanics");
        shop1.setAddress("2345 Austin Rd NE");
        shop1.setCity("Dallas");
        shop1.setState("Texas");
        shop1.setContractYears(5);

        return shop1;
    }
}
