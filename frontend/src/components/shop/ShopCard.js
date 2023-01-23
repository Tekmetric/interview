import PropTypes from "prop-types";
import { BASE_IMG_URL } from "constants";

ShopCard.propTypes = {
  shop: PropTypes.shape({
    title: PropTypes.string,
    avgOrder: PropTypes.number,
    staffNumber: PropTypes.number,
    location: PropTypes.string,
    imageFilename:PropTypes.string
  }),
};

export default function ShopCard({shop}) {
    const { title, avgOrder, staffNumber, location, imageFilename } = shop;
  return (
    <div className="card card-compact border w-72 bg-base-100 shadow-lg">
      <figure>
        <img className="h-28" src={`${BASE_IMG_URL}/shops/${imageFilename}`} alt="Shoes" />
      </figure>
      <div className="card-body">
        <p className="font-bold text-stone-400">{location}</p>
        <h2 className="card-title h-12">{title}</h2>
        <div className="divider m-0"></div>
        <div className="card-actions justify-between items-center">
          <div className="flex items-center">
            <div className="tooltip tooltip-primary" data-tip="Staff Number">
              <img className="w-6" src={`${BASE_IMG_URL}/people.png`} alt="people" />
            </div>
            <p className="font-bold mx-2">{staffNumber} People</p>
          </div>
          <div
            className="tooltip tooltip-primary badge badge-lg text-black text-orange-600"
            data-tip="Average Repair Order"
          >
            $ {avgOrder}
          </div>
        </div>
      </div>
    </div>
  );
}
