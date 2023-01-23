import Layout from "layout/Layout";
import { Link } from "react-router-dom";

export default function NotFoundPage() {
  return (
    <Layout>
      <div className="flex flex-col items-center my-20">
        <h1 className="text-6xl font-bold text-black leading-[5rem]">
          404
        </h1>
        <p className="py-6 mb-10 text-xl leading-10 text-left text-stone-500">Page Not Found</p>

        <Link to="/">
            <button className="btn btn-primary px-6 rounded-none active:bg-orange-700 text-white no-animation">Back to Home</button>
        </Link>

      </div>
    </Layout>
  );
}
