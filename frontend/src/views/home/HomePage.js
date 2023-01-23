import { BASE_IMG_URL } from "constants";
import Layout from "layout/Layout";
import { Link } from "react-router-dom";
import RoutesConfig from "routes";

export default function HomePage() {
  return (
    <Layout>
      <div className="hero py-12 mb-12">
        <div className="hero-content flex-col md:flex-row-reverse">
          <figure><img src={`${BASE_IMG_URL}/hero.webp`} className="max-w-full md:max-w-xl" alt="video"/></figure>
          <div>
            <h1 className="text-6xl font-bold text-black leading-[5rem]">With Tekmetric, Everyone Wins</h1>
            <p className="py-6 mb-10 text-xl leading-10 text-left text-stone-500">
              Empower your team, modernize your customer experience, and make
              better business decisions backed by real-time metrics. Instill
              processes that make life easier for your service advisors, your
              technicians, and your customers. Carry your shop in the palm of
              your hand so you can be there for your team from wherever life
              takes you.
              <br /><br />
              Tekmetric is <em className="font-bold">the</em> cloud-based shop
              management system designed for Auto Repair Professionals who are
              driving the industry forward.
            </p>
            <Link to={RoutesConfig.shopList}>
              <button className="btn btn-primary btn-lg px-10 rounded-none active:bg-orange-700 text-white no-animation">
                Find shops now
              </button>
            </Link>
          </div>
        </div>
      </div>

      <div className="flex justify-center mx-auto p-16 bg-sky-50">
        <div className="flex-col">
          <div className="flex gap-3 justify-between items-center mb-16">
            <h2 className="mt-5 mb-2 text-3xl font-medium leading-9 text-left">
              Unleash Your Shop's Potential
            </h2>
            <button className="btn btn-primary btn-outline btn-lg px-10 rounded-none active:bg-orange-700 text-white no-animation">
              Browse Features
            </button>
          </div>
          <div className="grid grid-cols-1 max-w-7xl md:grid-cols-2 lg:grid-cols-3 gap-4 lg:gap-8">
            <div className="p-4 rounded-md flex items-center justify-center">
              <div className="flex-col items-start">
                <img
                  src={`${BASE_IMG_URL}/3.svg`}
                  alt=""
                  className="mb-4 w-8 max-w-full"
                />
                <div>
                  <h3 className="mb-2 text-xl">
                    <strong className="font-bold leading-7">
                      Make Better Business Decisions
                    </strong>
                  </h3>
                  <p className="mb-2 text-stone-500 leading-6">
                    Track trends to discover key insights in seconds, and gain a
                    deeper understanding of how your decisions impact your
                    business, your team, and your customers. Make savvy
                    adjustments and wise investments with confidence.
                  </p>
                </div>
              </div>
            </div>

            <div className="p-4 rounded-md flex items-center justify-center">
              <div className="flex-col items-start">
                <img
                  src={`${BASE_IMG_URL}/4.svg`}
                  alt=""
                  className="mb-4 w-8 max-w-full"
                />
                <div>
                  <h3 className="mb-2 text-xl">
                    <strong className="font-bold leading-7">
                      Motivate Your Team
                    </strong>
                  </h3>
                  <p className="mb-2 text-stone-500 leading-6">
                    Lead with intention. Use real-time reports to improve shop
                    performance by setting clear expectations with your team.
                    Coach service advisors and technicians with constructive
                    feedback that helps them advance in their roles and their
                    careers.
                  </p>
                </div>
              </div>
            </div>

            <div className="p-4 rounded-md flex items-center justify-center">
              <div className="flex-col items-start">
                <img
                  src={`${BASE_IMG_URL}/5.svg`}
                  alt=""
                  className="mb-4 w-8 max-w-full"
                />
                <div>
                  <h3 className="mb-2 text-xl">
                    <strong className="font-bold leading-7">
                      Automated reports
                    </strong>
                  </h3>
                  <p className="mb-2 text-stone-500 leading-6">
                    Build trust and confidence in your work. Share Digital
                    Vehicle Inspections with photos and videos to show customers
                    exactly what needs to be repaired. Give them an experience
                    they'll want to recommend to their neighbors, friends, and
                    family.
                  </p>
                </div>
              </div>
            </div>

            <div className="p-4 rounded-md flex items-center justify-center">
              <div className="flex-col items-start">
                <img
                  src={`${BASE_IMG_URL}/1.svg`}
                  alt=""
                  className="mb-4 w-8 max-w-full"
                />
                <div>
                  <h3 className="mb-2 text-xl">
                    <strong className="font-bold leading-7">
                      Machine learning
                    </strong>
                  </h3>
                  <p className="mb-2 text-stone-500 leading-6">
                    Leverage mark-up matrices and declined jobs to widen your
                    margins, resulting in a higher ARO and cash flow. Discover
                    new opportunities to capture revenue and expand your
                    business.
                  </p>
                </div>
              </div>
            </div>

            <div className="p-4 rounded-md flex items-center justify-center">
              <div className="flex-col items-start">
                <img
                  src={`${BASE_IMG_URL}/0.svg`}
                  alt=""
                  className="mb-4 w-8 max-w-full"
                />
                <div>
                  <h3 className="mb-2 text-xl">
                    <strong className="font-bold leading-7">
                      Break Through Barriers
                    </strong>
                  </h3>
                  <p className="mb-2 text-stone-500 leading-6">
                    Move jobs efficiently by implementing workflow tools to
                    increase authorizations and speed up internal processes.
                    Accelerate toward your vision. Exceed your goals.
                  </p>
                </div>
              </div>
            </div>

            <div className="p-4 rounded-md flex items-center justify-center">
              <div className="flex-col items-start">
                <img
                  src={`${BASE_IMG_URL}/2.svg`}
                  alt=""
                  className="mb-4 w-8 max-w-full"
                />
                <div>
                  <h3 className="mb-2 text-xl">
                    <strong className="font-bold leading-7">
                      Move Forward Without Hold-Ups
                    </strong>
                  </h3>
                  <p className="mb-2 text-stone-500 leading-6">
                    The auto repair industry is built on customer service. Our
                    customer success team provides best-in-class service to you
                    so you can provide best-in-class service to your customers.
                  </p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </Layout>
  );
}
