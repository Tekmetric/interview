import { useEffect, useState } from 'react';
import { useNavigate } from "react-router-dom";
import UserType from '../types/UserType';
import AuthService from '../services/AuthService';
import * as Icon from 'react-bootstrap-icons';

export default function ProfilePage() {

    let navigate = useNavigate();
    const [currentUser, setCurrentUser] = useState<UserType | null>(null);

    useEffect(() => {
        const user = AuthService.getCurrentUser();
        if (user) {
            setCurrentUser(user);
        } else {
            navigate("/login", { replace: true });
        }
    }, []);

    function handleBack() {
        navigate("/", {replace: true});
        window.location.reload();
    }

    return (
        <div className="col-md-12">
            <div className="card card-container">
                <Icon.PersonFill size={100} className='banner-icon' />

                <form>
                    <div>
                        <div className="form-group">
                            <label htmlFor="firstName"> First Name </label>
                            <label  className="form-control">{currentUser?.firstName}</label>
                        </div>

                        <div className="form-group">
                            <label htmlFor="lastName"> Last Name </label>
                            <label className="form-control">{currentUser?.lastName}</label>
                        </div>

                        <div className="form-group">
                            <label htmlFor="email"> Email </label>
                            <label className="form-control">{currentUser?.email}</label>
                        </div>

                        <div className="form-group">
                            <label htmlFor="password"> Role </label>
                            <label className="form-control">{currentUser?.role}</label>
                        </div>

                        <div className="form-group">
                            <button type="button" className="btn btn-block" onClick={handleBack}>
                                <span>Back</span>
                            </button>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    );
}