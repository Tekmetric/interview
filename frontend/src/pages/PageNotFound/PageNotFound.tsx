import React from "react";
import Link from "../../components/Link/Link";

const PageNotFound = () => (
  <>
    <h4 className="text-md pt-2 text-rose-600">
      The page you are looking is not found.
    </h4>
    <Link to="/">Go to Query Screen</Link>
  </>
);

export default PageNotFound;
