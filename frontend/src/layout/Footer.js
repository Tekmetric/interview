import { LOGO_URL } from "constants";

export default function Footer() {
    return (
        <footer className="footer justify-evenly p-16 bg-gray-200  text-stone-600">
            <div>
                <img className="w-48 max-w-full mr-8" src={ LOGO_URL } alt="Tekmetric" />
                <p>Copyright © Tekmetric</p>
            </div> 
            <div>
                <span className="text-lg font-bold leading-7">Product</span> 
                <a href="##">Feature Overview</a> 
                <a href="##">Payment Processing</a> 
                <a href="##">Two-Way Texting</a> 
                <a href="##">Integrations</a>
                <a href="##">Pricing</a>
                <a href="##">Sign In</a>
            </div> 
            <div>
                <span className="text-lg font-bold leading-7">Resources</span> 
                <a href="##">About us</a> 
                <a href="##">Contact</a> 
                <a href="##">Jobs</a> 
                <a href="##">Press kit</a>
            </div> 
            <div>
                <span className="text-lg font-bold leading-7">Company</span> 
                <a href="##">Terms of use</a> 
                <a href="##">Privacy policy</a> 
                <a href="##">Cookie policy</a>
            </div> 
            <div>
                <span className="text-lg font-bold leading-7">Subscribe to our newsletter</span> 
                <div className="form-control w-80">
                    <label className="label">
                    <span className="label-text">The latest news, articles, and resources, sent to your inbox.</span>
                    </label> 
                    <div className="relative">
                        <input type="text" placeholder="Email Address" className="input input-bordered w-full pr-16" /> 
                        <button className="btn btn-primary absolute top-0 right-0 rounded-none">Subscribe</button>
                    </div>
                </div>
            </div>
        </footer>
    );
}
