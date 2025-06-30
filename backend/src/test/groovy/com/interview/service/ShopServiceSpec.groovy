package com.interview.service

import com.interview.domain.dto.ShopDto
import com.interview.domain.mapper.ShopMapper
import com.interview.domain.entity.Shop
import com.interview.repository.ShopRepository
import spock.lang.Specification

class ShopServiceSpec extends Specification {

    ShopRepository shopRepository = Mock()
    ShopMapper shopMapper = Mock()
    ShopService shopService = new ShopService(shopRepository, shopMapper)

    def "findAll returns list of ShopDto"() {
        given:
        def shop1 = new Shop(id: 1L, name: 'A', address: 'X', numberOfEmployees: 5)
        def shop2 = new Shop(id: 2L, name: 'B', address: 'Y', numberOfEmployees: 3)
        def shops = [shop1, shop2]
        def dtos = shops.collect { new ShopDto(it.id, it.name, it.address, it.numberOfEmployees) }
        shopRepository.findAll() >> shops
        shopMapper.toDtoList(shops) >> dtos

        when:
        def result = shopService.findAll()

        then:
        result == dtos
    }

    def "save delegates to repository and mapper"() {
        given:
        def dto = new ShopDto(null, 'Name', 'Addr', 5)
        def entity = new Shop(name: 'Name', address: 'Addr', numberOfEmployees: 5)
        def savedEntity = new Shop(id: 1L, name: 'Name', address: 'Addr', numberOfEmployees: 5)
        def savedDto = new ShopDto(1L, 'Name', 'Addr', 5)
        shopMapper.toEntity(dto) >> entity
        shopRepository.save(entity) >> savedEntity
        shopMapper.toDto(savedEntity) >> savedDto

        when:
        def result = shopService.save(dto)

        then:
        result == savedDto
    }

    def "update returns optional updated entity"() {
        given:
        def existing = new Shop(id: 1L, name: 'Old', address: 'OldAddr', numberOfEmployees: 1)
        def dto = new ShopDto(null, 'New', 'NewAddr', 10)
        shopRepository.findById(1L) >> Optional.of(existing)
        shopRepository.save(_ as Shop) >> { Shop s -> s }

        when:
        def result = shopService.update(1L, dto)

        then:
        result.present
        def updated = result.get()
        updated.name == 'New'
        updated.address == 'NewAddr'
        updated.numberOfEmployees == 10
    }
   
    def "update sets fields correctly for various inputs"() {
        given:
        def existing = new Shop(id: 1L, name: 'Init', address: 'InitAddr', numberOfEmployees: 0)
        shopRepository.findById(1L) >> Optional.of(existing)
        shopRepository.save(_ as Shop) >> { Shop s -> s }

        when:
        def result = shopService.update(1L, new ShopDto(null, newName, newAddr, newNum))

        then:
        result.present
        def updated = result.get()
        updated.name == newName
        updated.address == newAddr
        updated.numberOfEmployees == newNum

        where:
        newName | newAddr   | newNum
        'Alice' | 'Street1' | 10
        'Bob'   | 'Street2' | 20
        'Carol' | 'Street3' | 0
        'Dave'  | 'Street4' | 100
        'Eve'   | 'Street5' | 5
    }
}
