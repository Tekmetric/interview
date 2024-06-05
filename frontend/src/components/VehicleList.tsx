import { useEffect, useState } from 'react';
import { useNavigate } from "react-router-dom";
import AuthService from '../services/AuthService';
import VehicleService from '../services/VehicleService';
import VehicleType from '../types/VehicleType';
import { stateStr } from '../types/VehicleType';
import Pagination from '../types/Pagination';
import currencyFormatter from '../utils/CurrencyFormatter';
import * as Icon from 'react-bootstrap-icons';
import DeleteVehicleWarning from './DeleteVehicleWarning';
import './VehicleList.css';

export default function VehicleList() {

    let navigate = useNavigate();
    const [vehicles, setVehicles] = useState<VehicleType[]>([]);
    const [deletingVehicle, setDeletingVehicle] = useState<number | null>(null);
    const [pagination, setPagination] = useState<Pagination>(new Pagination(0, 10));

    function queryData(pageNumber: number, pageSize: number) {
        VehicleService.getVehicles(pageNumber, pageSize).then(response => {
            if (response.data.content) {
                setVehicles(response.data.content);
            }

            setPagination({
                empty: response.data.empty,
                first: response.data.first,
                last: response.data.last,
                numberOfElements: response.data.numberOfElements,
                totalElements: response.data.totalElements,
                totalPages: response.data.totalPages,
                pageSize: response.data.pageable.pageSize,
                pageNumber: response.data.pageable.pageNumber
            });
        },
        error => {
            const status = error.response?.status;
            const errorMessage = error.response?.data?.message || error.message || error.toString();
            if (status === 403 || status === 401) {
                navigate("/login", { replace: true });
            }
            console.log(errorMessage);
        });
    }

    useEffect(() => {
        const user = AuthService.getCurrentUser();
        if (user) {
            queryData(0, 10);

        } else {
            navigate("/login", { replace: true });
        }
    }, []);

    function handleNewVehicle() {
        navigate("/vehicle/create", { replace: true });
    }

    function handleEditVehicle(id: number) {
        navigate("/vehicle/edit/" + id , { replace: true });
    }

    function handleDeleteVehicle(id: number, cancelled: false) {
        if (cancelled) {
            setDeletingVehicle(null);
        } else {
            VehicleService.deleteVehicle(id).then(() => {
                setDeletingVehicle(null);
                handlePageClick(pagination.pageNumber);
            });
        }
    }

    function handlePageClick(pageIndex: number) {
        queryData(pageIndex, pagination.pageSize);
    }

    const vehicleRows = vehicles.map(vehicleDto => {
        return <tr key={vehicleDto.id}>
            <td>{stateStr(vehicleDto.state)}</td>
            <td>{vehicleDto.licensePlate}</td>
            <td>{vehicleDto.brand} {vehicleDto.model}</td>
            <td>{vehicleDto.registrationYear}</td>
            <td>{currencyFormatter(vehicleDto.cost)}</td>
            <td>{(new Date(vehicleDto.creationDate)).toLocaleDateString()}</td>
            <td>
                <a href="#" onClick={() => handleEditVehicle(vehicleDto.id)} className="edit" data-toggle="modal">
                    <Icon.PencilFill color="orange" data-toggle="tooltip" title="Edit" />
                </a>
                <a href="#" onClick={() => setDeletingVehicle(vehicleDto.id)} className="delete" data-toggle="modal">
                    <Icon.TrashFill color="red" data-toggle="tooltip" title="Delete" />
                </a>
            </td>
        </tr>;
    });

    const paginationLinks = Array.from(Array(pagination.totalPages).keys()).map((i) => {
        return <li key={i} className={pagination?.pageNumber === i ? 'page-item active' : 'page-item'}>
            <a href="#" onClick={() => handlePageClick(i)} className="page-link">{i + 1}</a>
        </li>
    });

    return (
        <div className="container-xl card">
            <div className="table-responsive">
                <div className="table-wrapper">
                    <div className="table-title">
                        <div className="row">
                            <div className="col-sm-6">
                                <h2>Manage Vehicles</h2>
                            </div>
                            <div className="col-sm-6">
                                <a href="#" className="btn btn-success" onClick={handleNewVehicle}>
                                    <Icon.PlusCircleFill color="white" /> <span>New Vehicle</span>
                                </a>
                            </div>
                        </div>
                    </div>
                    <table className="table table-striped table-hover">
                        <thead>
                            <tr>
                                <th>State</th>
                                <th>License Plate</th>
                                <th>Model</th>
                                <th>Year</th>
                                <th>Cost</th>
                                <th>Creation Date</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            {vehicleRows}
                        </tbody>
                    </table>

                    {pagination && (
                        <div className="clearfix">
                            <div className="hint-text">
                                Showing&nbsp;
                                <b>{pagination.pageNumber * pagination.pageSize}</b> to&nbsp; 
                                <b>{pagination.pageNumber * pagination.pageSize + pagination.numberOfElements}</b> out of&nbsp; 
                                <b>{pagination.totalElements}</b> entries
                            </div>
                            <ul className="pagination">
                                <li className={pagination.first ? 'page-item disabled' : 'page-item'}>
                                    <a href="#" onClick={() => handlePageClick(pagination.pageNumber - 1)} className="page-link">&#60;</a>
                                </li>
                                {paginationLinks}
                                <li className={pagination.last ? 'page-item disabled' : 'page-item'}>
                                    <a href="#" onClick={() => handlePageClick(pagination.pageNumber + 1)} className="page-link">&#62;</a>
                                </li>
                            </ul>
                        </div>
                    )}
                </div>
            </div>
            <DeleteVehicleWarning vehicleId={deletingVehicle} onDelete={handleDeleteVehicle} />
        </div>
    );
}