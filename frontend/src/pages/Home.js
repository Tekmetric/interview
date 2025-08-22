import React, { useState, useEffect, Fragment } from 'react';
import { Routes, Route, Link } from 'react-router-dom';
import VehiclePage from './VehiclePage';
import { Header } from '../components';
import { API_BASE_URL } from '../config';
import './Home.scss';

function App() {
  const [vehicles, setVehicles] = useState([]);
  const [isLoadingVehicles, setIsLoadingVehicles] = useState(true);
  const [vehiclesError, setVehiclesError] = useState(null);
  const [selectedVehicle, setSelectedVehicle] = useState(null);

  useEffect(() => {
    fetchVehicles();
  }, []);

  const fetchVehicles = async () => {
    try {
      const vehiclesUrl = process.env.REACT_APP_UPLOAD_URL || API_BASE_URL;
      const response = await fetch(`${vehiclesUrl}?get_vehicles=1`);
      
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      
      const data = await response.json();
      setVehicles(data);
      setVehiclesError(null);
    } catch (error) {
      console.error('Error fetching vehicles:', error);
      setVehiclesError(error.message);
    } finally {
      setIsLoadingVehicles(false);
    }
  };

  const handleVehicleSelect = (vehicle) => {
    setSelectedVehicle(selectedVehicle?.id === vehicle.id ? null : vehicle);
  };

  const handleRefreshVehicles = () => {
    setIsLoadingVehicles(true);
    fetchVehicles();
  };

  return (
    <div className="App">
      <Routes>
        <Route path="/" element={
          <>
            <Header
              title="Vehicle List"
              subtitle="Select a vehicle to view and annotate images"
            />
            <div className="main-content">
              <div className="vehicles-section">
                <div className="vehicles-actions">
                  <h3>Vehicles</h3>
                  <button 
                    className="refresh-vehicles-btn"
                    onClick={handleRefreshVehicles}
                    disabled={isLoadingVehicles}
                  >
                    {isLoadingVehicles ? 'Loading...' : 'Refresh'}
                  </button>
                </div>
                
                {isLoadingVehicles && (
                  <div className="loading-message">Loading vehicles...</div>
                )}
                
                {vehiclesError && (
                  <div className="error-message">Error: {vehiclesError}</div>
                )}
                
                {!isLoadingVehicles && !vehiclesError && vehicles.length === 0 && (
                  <div className="no-vehicles-message">No vehicles found.</div>
                )}
                
                {!isLoadingVehicles && !vehiclesError && vehicles.length > 0 && (
                  <div className="vehicles-table-container">
                    <table className="vehicles-table">
                      <thead>
                        <tr>
                          <th>ID</th>
                          <th>Make</th>
                          <th>Model</th>
                          <th>Year</th>
                          <th>VIN</th>
                          <th>License Plate</th>
                          <th>Images / Annotations</th>
                          <th>Actions</th>
                        </tr>
                      </thead>
                      <tbody>
                        {vehicles.map((vehicle) => (
                          <Fragment key={vehicle.id}>
                            <tr className="vehicle-row" onClick={() => handleVehicleSelect(vehicle)}>
                              <td>{vehicle.id}</td>
                              <td>{vehicle.make}</td>
                              <td>{vehicle.model}</td>
                              <td>{vehicle.year}</td>
                              <td className="vin-cell">{vehicle.vin}</td>
                              <td>{vehicle.license_plate}</td>
                              <td>{vehicle.images?.length || 0} / {vehicle.total_annotations}</td>
                              <td>
                                <button
                                  className="select-vehicle-btn"
                                  onClick={() => handleVehicleSelect(vehicle)}
                                >
                                  Quick look
                                </button> { ' ' }
                                <Link className="view-vehicle-btn" to={`/vehicle/${vehicle.id}/`}>View Vehicle</Link>
                              </td>
                            </tr>
                            {selectedVehicle?.id === vehicle.id && (
                              <tr>
                                <td colSpan="8">
                                  <div className="vehicle-images-section">
                                    {vehicle.images && vehicle.images.length > 0 ? (
                                      <div className="vehicle-images-grid">
                                        {vehicle.images.map((image, index) => (
                                          <div key={index} className="vehicle-image-container">
                                            <Link to={`/vehicle/${vehicle.id}/`}>
                                            <img 
                                              src={image.src} 
                                              alt={`Vehicle ${index + 1}`}
                                              className="vehicle-image"
                                            />
                                            </Link>
                                            {image.markers && (
                                              <div className='image-markers'>
                                                {image.markers.red > 0 && (<span className='image-marker-red'>{image.markers.red}</span>)}
                                                {image.markers.yellow > 0 && (<span className='image-marker-yellow'>{image.markers.yellow}</span>)}
                                                {image.markers.green > 0 && (<span className='image-marker-green'>{image.markers.green}</span>)}
                                              </div>
                                            )}
                                          </div>
                                        ))}
                                      </div>
                                    ) : (
                                      <div className="no-images-message">No images available for this vehicle. <Link to={`/vehicle/${vehicle.id}/`}>View Vehicle and Upload Images</Link></div>
                                    )}
                                  </div>
                                </td>
                              </tr>
                            )}
                          </Fragment>
                        ))}
                      </tbody>
                    </table>
                  </div>
                )}
              </div>
            </div>
          </>
        } />
        <Route path="/vehicle/:id" element={<VehiclePage />} />
      </Routes>
    </div>
  );
}

export default App;
