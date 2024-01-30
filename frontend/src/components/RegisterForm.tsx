import { useState } from 'react';
import { useNavigate } from "react-router-dom";
import { Formik, Field, Form, ErrorMessage } from "formik";
import * as Yup from "yup";
import FormUtil from '../utils/FormUtils';
import AuthService from "../services/AuthService";
import * as Icon from 'react-bootstrap-icons';

export default function RegisterForm() {

    let navigate = useNavigate();
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [firstName, setFirstName] = useState('');
    const [lastName, setLastName] = useState('');
    const [formUtil, setFormUtil] = useState(new FormUtil(false, ''));

    function validationSchema() {
        return Yup.object().shape({
            firstName: Yup.string()
                .required("This field is required!"),
            lastName: Yup.string()
                .required("This field is required!"),
            email: Yup.string()
                .email("This is not a valid email.")
                .required("This field is required!"),
            password: Yup.string()
                .test(
                    "len",
                    "The password must be between 8 and 50 characters.",
                    (val: any) =>
                        val &&
                        val.toString().length >= 8 &&
                        val.toString().length <= 50
                )
                .required("This field is required!"),
        });
    }

    function handleRegister(formValues: { firstName: string, lastName: string, email: string; password: string }) {
        setFormUtil(new FormUtil(true, ''));

        AuthService.register(formValues.email, formValues.password, formValues.firstName, formValues.lastName).then(
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
                    initialValues={{ firstName: '', lastName: '', email: '', password: '' }}
                    validationSchema={validationSchema}
                    onSubmit={handleRegister}
                >
                    <Form>
                        <div>
                            <div className="form-group">
                                <label htmlFor="firstName"> First Name </label>
                                <Field name="firstName" type="text" className="form-control" />
                                <ErrorMessage
                                    name="firstName"
                                    component="div"
                                    className="alert alert-danger"
                                />
                            </div>

                            <div className="form-group">
                                <label htmlFor="lastName"> Last Name </label>
                                <Field name="lastName" type="text" className="form-control" />
                                <ErrorMessage
                                    name="lastName"
                                    component="div"
                                    className="alert alert-danger"
                                />
                            </div>

                            <div className="form-group">
                                <label htmlFor="email"> Email </label>
                                <Field name="email" type="email" className="form-control" />
                                <ErrorMessage
                                    name="email"
                                    component="div"
                                    className="alert alert-danger"
                                />
                            </div>

                            <div className="form-group">
                                <label htmlFor="password"> Password </label>
                                <Field
                                    name="password"
                                    type="password"
                                    className="form-control"
                                />
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
                                    <span>Register</span>
                                </button>
                            </div>
                            <div className="form-group">
                                <button type="button" className="btn btn-block" onClick={() => navigate("/login", {replace: true})}>
                                    <span>Login</span>
                                </button>
                            </div>
                        </div>

                        {formUtil.message && (
                            <div className="form-group">
                                <div
                                    className="alert alert-danger"
                                    role="alert"
                                >
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