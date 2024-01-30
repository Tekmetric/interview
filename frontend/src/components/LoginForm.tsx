import { useState } from 'react';
import { useNavigate } from "react-router-dom";
import { Formik, Field, Form, ErrorMessage } from "formik";
import * as Yup from "yup";
import FormUtil from '../utils/FormUtils';
import AuthService from "../services/AuthService";
import * as Icon from 'react-bootstrap-icons';

export default function LoginForm() {

    let navigate = useNavigate();
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [formUtil, setFormUtil] = useState(new FormUtil(false, ''));

    function validationSchema() {
        return Yup.object().shape({
            email: Yup.string().required("This field is required!"),
            password: Yup.string().required("This field is required!"),
        });
    }

    function handleLogin(formValues: { email: string; password: string }) {
        setFormUtil(new FormUtil(true, ''));

        AuthService.login(formValues.email, formValues.password).then(
            () => {
                navigate("/", {replace: true});
                window.location.reload();
            },
            error => {
                const errorMessage = error.response?.data?.message || error.message || error.toString();
                setFormUtil(new FormUtil(false, errorMessage));
            }
        );
    }

    return (
        <div className="col-md-12">
            <div className="card card-container">
            <Icon.PersonFill size={100} className='banner-icon' />

                <Formik
                    initialValues={{ email: '', password: '' }}
                    validationSchema={validationSchema}
                    onSubmit={handleLogin}
                >
                    <Form>
                        <div className="form-group">
                            <label htmlFor="email">Email</label>
                            <Field name="email" type="text" className="form-control" />
                            <ErrorMessage
                                name="email"
                                component="div"
                                className="alert alert-danger"
                            />
                        </div>

                        <div className="form-group">
                            <label htmlFor="password">Password</label>
                            <Field name="password" type="password" className="form-control" />
                            <ErrorMessage
                                name="password"
                                component="div"
                                className="alert alert-danger"
                            />
                        </div>

                        <div className="form-group">
                            <button type="submit" className="btn btn-primary btn-block" disabled={formUtil.loading}>
                                {formUtil.loading && (
                                    <span className="spinner-border spinner-border-sm"></span>
                                )}
                                <span>Login</span>
                            </button>
                        </div>
                        <div className="form-group">
                            <button type="button" className="btn btn-block" onClick={() => navigate("/register", {replace: true})}>
                                <span>Register</span>
                            </button>
                        </div>

                        {formUtil.message && (
                            <div className="form-group">
                                <div className="alert alert-danger" role="alert">
                                    {formUtil.message}
                                </div>
                            </div>
                        )}
                    </Form>
                </Formik>
            </div>
        </div>
    );
}