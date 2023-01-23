import { LOGO_URL, BASE_IMG_URL } from "constants";
import { Link } from "react-router-dom";
import { useNavigate } from 'react-router-dom';
import { useAuth } from 'contexts/authContext';
import { authService } from 'api/authService';
import RoutesConfig from "routes";

export default function Header() {
    const { auth, logout } = useAuth();
    const navigate = useNavigate();

    const onSignOut = async () => {
        try {
          const { status } = await authService.logout();
          if (status === 200) {
            navigate('/', { replace: true });
          }
        } catch (error) {
          console.error(error.message);
        } finally {
          logout();
        }
    };

    return (
        <div className="flex flex-1 justify-evenly top-0 mx-auto max-w-none py-9 shadow-[0_1px_3px_0_rgba(0,0,0,0.1)]">
            <div className="flex items-center my-auto">
                <Link to="/"><img className="w-48 max-w-full mr-8" src={LOGO_URL} alt="Tekmetric" /></Link>
                <div className="flex-none hidden lg:block mr-8">
                    <ul className="flex justify-between gap-8 text-slate-700 px-1">
                    <li><a href="##" className="hover:text-sky-600">Features</a></li>
                    <li><a href="##" className="hover:text-sky-600">Community</a></li>
                    <li><a href="##" className="hover:text-sky-600">Resources</a></li>
                    <li><a href="##" className="hover:text-sky-600">Pricing</a></li>
                    </ul>
                </div>
                <div className="flex justify-evenly items-center">
                    <a href="tel:(832)981-4617">
                    <div className="flex mr-4 hover:text-sky-600">                
                        <figure><img className="w-5 mr-2" src="/assets/img/call.svg" alt="call" /></figure>
                        (832) 598-6420
                    </div>
                    </a>
                    { !auth ? ( 
                        <>
                            <div className="mr-4 hidden lg:block" >|</div>
                            <Link to="/login" className="text-black font-medium mr-4 hover:text-sky-600 hidden lg:block">Sign in</Link>
                            <Link to="/login">
                                <button className="hidden lg:block text-orange-600 border border-orange-600 hover:bg-orange-600 hover:text-white active:bg-orange-700 px-8 py-3 rounded outline-none focus:outline-none mr-1 mb-1 ease-linear duration-150">
                                    Get started
                                </button>
                            </Link>
                        </> ) : (
                        <>
                            <div className="dropdown dropdown-end">
                                <label tabIndex={0} className="btn btn-ghost btn-circle avatar">
                                    <div className="w-6 pb-1 rounded-full">
                                    <img src={`${BASE_IMG_URL}/avatar.svg`} alt="Profile"/>
                                    </div>
                                </label>
                                <ul tabIndex={0} className="mt-3 p-1 shadow menu menu-compact dropdown-content bg-base-100 rounded-none w-52">
                                    <li><Link to={RoutesConfig.shopList}>Shops</Link></li>
                                    <li><Link onClick={onSignOut}>Sign Out</Link></li>
                                </ul>
                            </div>
                        </>
                    )}
                </div>
            </div>
        </div>
    );
}

