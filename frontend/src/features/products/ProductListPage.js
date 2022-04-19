import React, {useEffect, useState} from 'react';
import {useGetRequest} from "../../hooks";
import {CircularProgress, Pagination, Stack, Typography} from "@mui/material";
import ProductItem from "./ProductItem";

const ProductListPage = () => {
    const PER_PAGE = 3;
    const [pageInfo, setPageInfo] = useState({totalCount: 0, products: []});
    const [currentPageIdx, setCurrentPageIdx] = useState(1);

    const {loading, performGet} = useGetRequest();

    useEffect(() => {
        const getResponse = async () => {
            const countResponse = await performGet('http://localhost:8080/products/totalCount');

            if (parseInt(countResponse.status) === 200 && parseInt(countResponse.data) > 0) {
                const productsResponse = await performGet('http://localhost:8080/products', {
                    params: {
                        page: currentPageIdx - 1,
                        pageSize: PER_PAGE
                    }
                });
                setPageInfo({totalCount: countResponse.data, products: productsResponse.data})
            }
        }
        getResponse().catch(console.error);

    }, [currentPageIdx]);


    const handlePageChange = (event, value) => {
        setCurrentPageIdx(value);
    };

    return (
        <div>
            <h1>Product List Page</h1>
            {loading && (<CircularProgress/>)}
            {!loading && (

                <div>
                    <Typography>Page: {currentPageIdx}</Typography>

                    <section className="productlist">
                        {
                            pageInfo.products.map((product) => {
                                return <ProductItem key={product.id} {...product}/>;
                            })
                        }
                    </section>
                    <div className="footer">
                        <Stack spacing={2}>
                            <Pagination page={currentPageIdx} onChange={handlePageChange}
                                        count={Math.ceil(pageInfo.totalCount / PER_PAGE)}/>
                        </Stack>
                    </div>
                </div>

            )}

        </div>
    );
};

export default ProductListPage;
