import React, { useEffect, useState } from "react";
import { shopService } from "api/shopService";
import toast from "react-hot-toast";
import ShopList from "components/shop/ShopList";
import Layout from "layout/Layout";
import { DebounceInput } from "react-debounce-input";

export default function ShopListPage() {
  const [search, setSearch] = useState("");
  const [shops, setShops] = useState();
  const [pages, setPages] = useState(10);
  const [loading, setLoading] = useState(true);
  const [pageIndex, setPageIndex] = useState(0);

  useEffect(() => {
    async function fetchData() {
      try {
        const { status, data } = await shopService.search(search, pageIndex);
        setLoading(false);
        if (status === 200) {
          setShops(data.content);
          setPages(data.totalPages);
        } else {
          console.warn(status, data.message);
        }
      } catch (error) {
        toast.error(error.response.data.message);
        console.log(error.message);
      }
    }
    setLoading(true);
    fetchData();
  }, [search, pageIndex]);

  const handlePageChange = (newPage) => {
    setPageIndex(newPage);
  };

  const handleSearch = (search) => {
    setSearch(search);
    setPageIndex(0);
  };

  return (
    <Layout>
      <div className="flex flex-col justify-center items-center mx-auto my-6">
        <div className="mt-5 mb-4">
          <h1 className="text-5xl font-bold text-center text-neutral-900">
            Tekmetric Shop Spotlights
          </h1>
          <div className="text-xl tracking-normal leading-7 text-center text-stone-500">
            As Seen in Ratchet + Wrench Magazine
          </div>
        </div>
        <div className="my-10 relative mx-auto text-gray-600 text-center">
          <DebounceInput
            className="border-2 bg-white w-96 h-10 px-5  rounded-lg text-sm focus:outline-none"
            debounceTimeout={800}
            onChange={(event) => handleSearch(event.target.value)}
            placeholder="Search Shops"
            autoFocus
            autoComplete="off"
          />
        </div>

        {shops?.length ? (
          <ShopList shops={shops} loading={loading} />
          ): (
            <p>No result</p>
          )}

        {shops && !loading && (
          <div className="btn-group my-12">
            {[...Array(pages)].map((_, index) => (
              <button
                className={`btn px-6 btn-md no-animation bg-stone-100 hover:btn-primary border-none text-stone-600 ${pageIndex === index ? "btn-active" : ""}`}
                key={index}
                onClick={() => handlePageChange(index)}
              >
                {index + 1}
              </button>
            ))}
          </div>
        )}
      </div>
    </Layout>
  );
}
