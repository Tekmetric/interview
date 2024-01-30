import { useState, useEffect } from 'react';
import { useParams, useNavigate } from "react-router-dom";
import { Formik, Field, Form, ErrorMessage } from "formik";
import FormUtil from '../utils/FormUtils';
import VehicleService from '../services/VehicleService';
import * as Yup from "yup";
import * as Icon from 'react-bootstrap-icons';
import VehicleType from '../types/VehicleType';

type PathParams = {
    id: string;
};

export default function EditVehicleForm() {

    let navigate = useNavigate();
    const { id } = useParams<PathParams>();
    const [licensePlate, setLicensePlate] = useState('');
    const [brand, setBrand] = useState('');
    const [model, setModel] = useState('');
    const [registrationYear, setRegistrationYear] = useState(2000);
    const [cost, setCost] = useState(0.0);
    const [vehicle, setVehicle] = useState<VehicleType | null>(null);
    const [formUtil, setFormUtil] = useState(new FormUtil(false, ''));

    useEffect(() => {
        if (id) {
            VehicleService.getVehicle(parseInt(id)).then(
                response => {
                    setVehicle(response.data);
                },
                error => {
                    const errorMessage = error.response?.data?.message || error.message || error.toString();
                    setFormUtil(new FormUtil(false, errorMessage));
                }
            );

        } else {
            handleCancel();
        }
    }, []);

    function validationSchema() {
        return Yup.object().shape({
            licensePlate: Yup.string()
                .test(
                    "len",
                    "The license plate must be between 6 and 10 characters.",
                    (val: any) =>
                        val &&
                        val.toString().length >= 6 &&
                        val.toString().length <= 10
                )
                .required("This field is required!"),
            brand: Yup.string()
                .required("This field is required!"),
            model: Yup.string()
                .required("This field is required!"),
            registrationYear: Yup.number()
                .min(1900)
                .max(2100)
                .required("This field is required!"),
            cost: Yup.number()
                .min(10)
                .required("This field is required!"),
        });
    }

    function handleEdit(formValues: any) {
        if (!id) return;

        setFormUtil(new FormUtil(true, ''));

        VehicleService.updateVehicle(
            parseInt(id),
            formValues.licensePlate,
            formValues.brand,
            formValues.model,
            formValues.registrationYear,
            formValues.cost
        ).then(
            () => {
                navigate("/", { replace: true });
                window.location.reload();
            },
            error => {
                const errorMessage = error.response?.data?.message || error.message || error.toString();
                setFormUtil(new FormUtil(false, errorMessage));
            }
        );
    }

    function handleCancel() {
        navigate("/", { replace: true });
        window.location.reload();
    }

    return (
        <div className="col-md-12">
            <div className="card card-container">
                <Icon.CarFront size={100} className='banner-icon' />

                {vehicle && (
                    <Formik
                        initialValues={vehicle}
                        validationSchema={validationSchema}
                        onSubmit={handleEdit}
                    >
                        <Form>
                            <div>
                                <div className="form-group">
                                    <label htmlFor="licensePlate"> License Plate </label>
                                    <Field name="licensePlate" type="text" className="form-control" />
                                    <ErrorMessage
                                        name="licensePlate"
                                        component="div"
                                        className="alert alert-danger"
                                    />
                                </div>

                                <div className="form-group">
                                    <label htmlFor="brand"> Brand </label>
                                    <Field name="brand" type="text" className="form-control" />
                                    <ErrorMessage
                                        name="brand"
                                        component="div"
                                        className="alert alert-danger"
                                    />
                                </div>

                                <div className="form-group">
                                    <label htmlFor="model"> Model </label>
                                    <Field name="model" type="text" className="form-control" />
                                    <ErrorMessage
                                        name="model"
                                        component="div"
                                        className="alert alert-danger"
                                    />
                                </div>

                                <div className="form-group">
                                    <label htmlFor="registrationYear"> Registration Year </label>
                                    <Field name="registrationYear" type="number" className="form-control" />
                                    <ErrorMessage
                                        name="registrationYear"
                                        component="div"
                                        className="alert alert-danger"
                                    />
                                </div>

                                <div className="form-group">
                                    <label htmlFor="cost"> Cost </label>
                                    <Field name="cost" type="number" className="form-control" />
                                    <ErrorMessage
                                        name="cost"
                                        component="div"
                                        className="alert alert-danger"
                                    />
                                </div>
                                <div className="form-group">
                                    <button type="submit" className="btn btn-primary btn-block" disabled={formUtil.loading}>
                                        {formUtil.loading && (
                                            <span className="spinner-border spinner-border-sm"></span>
                                        )}
                                        <span>Update</span>
                                    </button>
                                </div>
                                <div className="form-group">
                                    <button type="button" className="btn btn-light btn-block" onClick={handleCancel} disabled={formUtil.loading}>
                                        {formUtil.loading && (
                                            <span className="spinner-border spinner-border-sm"></span>
                                        )}
                                        <span>Cancel</span>
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
                )}
            </div>
        </div>
    );
}