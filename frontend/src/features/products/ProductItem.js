import React from 'react';

const ProductItem = (props) => {
    const {imageUrl, name, price} = props;
    return (
        <article className="productItem">
            <img src={imageUrl} alt="..."/>
            <h1>{name}</h1>
            <h4>{price}</h4>
        </article>
    );
};

export default ProductItem;
