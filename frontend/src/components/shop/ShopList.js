import PropTypes from "prop-types";
import ShopCard from "./ShopCard";

ShopList.propTypes = {
  loading: PropTypes.bool,
  shops: PropTypes.array,
};

export default function ShopList({ shops, loading }) {
  return (
    <>
      {loading ? (
        <p className="text-center">Loading...</p>
      ) : (
        <div className="grid gap-12 grid-cols-1 sm:grid-cols-3 place-items-center">
          {shops.map((shop, index) => <ShopCard key={shop.id} shop={shop} />)}
        </div>
      )}
    </>
  );
}
