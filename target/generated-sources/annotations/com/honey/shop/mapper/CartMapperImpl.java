package com.honey.shop.mapper;

import com.honey.shop.domain.Cart;
import com.honey.shop.domain.CartItem;
import com.honey.shop.domain.Product;
import com.honey.shop.dto.response.CartItemResponse;
import com.honey.shop.dto.response.CartResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-15T14:31:19+0100",
    comments = "version: 1.6.3, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class CartMapperImpl implements CartMapper {

    @Override
    public CartResponse toResponse(Cart cart) {
        if ( cart == null ) {
            return null;
        }

        CartResponse.CartResponseBuilder cartResponse = CartResponse.builder();

        cartResponse.id( cart.getId() );
        cartResponse.token( cart.getToken() );
        cartResponse.createdAt( cart.getCreatedAt() );
        cartResponse.updatedAt( cart.getUpdatedAt() );
        cartResponse.items( cartItemListToCartItemResponseList( cart.getItems() ) );

        cartResponse.totalPrice( computeTotal(cart) );

        return cartResponse.build();
    }

    @Override
    public CartItemResponse toItemResponse(CartItem item) {
        if ( item == null ) {
            return null;
        }

        CartItemResponse.CartItemResponseBuilder cartItemResponse = CartItemResponse.builder();

        cartItemResponse.productId( itemProductId( item ) );
        cartItemResponse.productName( itemProductName( item ) );
        cartItemResponse.unitPrice( itemProductPrice( item ) );
        cartItemResponse.id( item.getId() );
        cartItemResponse.quantity( item.getQuantity() );

        cartItemResponse.subtotal( subtotal(item) );

        return cartItemResponse.build();
    }

    protected List<CartItemResponse> cartItemListToCartItemResponseList(List<CartItem> list) {
        if ( list == null ) {
            return null;
        }

        List<CartItemResponse> list1 = new ArrayList<CartItemResponse>( list.size() );
        for ( CartItem cartItem : list ) {
            list1.add( toItemResponse( cartItem ) );
        }

        return list1;
    }

    private Long itemProductId(CartItem cartItem) {
        Product product = cartItem.getProduct();
        if ( product == null ) {
            return null;
        }
        return product.getId();
    }

    private String itemProductName(CartItem cartItem) {
        Product product = cartItem.getProduct();
        if ( product == null ) {
            return null;
        }
        return product.getName();
    }

    private BigDecimal itemProductPrice(CartItem cartItem) {
        Product product = cartItem.getProduct();
        if ( product == null ) {
            return null;
        }
        return product.getPrice();
    }
}
