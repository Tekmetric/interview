import React, { useState, useEffect, useCallback, useRef } from 'react';
import { useParams, Link } from 'react-router-dom';
import AnnotationOverlay from '../components/AnnotationOverlay';
import { Header, AnnotationsTable } from '../components';
import { API_BASE_URL } from '../config';
import './VehiclePage.scss';

export default function VehiclePage() {
  const { id } = useParams();
  const [vehicle, setVehicle] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);
  const [selectedFile, setSelectedFile] = useState(null);
  const [isUploading, setIsUploading] = useState(false);
  const [uploadResult, setUploadResult] = useState(null);
  const [uploadError, setUploadError] = useState(null);
  const [allAnnotations, setAllAnnotations] = useState({});
  const [isEditMode, setIsEditMode] = useState(false);
  
  // Throttling mechanism for API calls
  const saveTimeouts = useRef({});
  const pendingUpdates = useRef({});

  const fetchVehicle = useCallback(async () => {
    try {
      setIsLoading(true);
      const response = await fetch(`${API_BASE_URL}?get_vehicle=${id}`);
      
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      
      const data = await response.json();
      setVehicle(data);
      setError(null);
      
      // Load annotations for all images from API
      if (data.images && Array.isArray(data.images)) {
        try {
          const initialAnnotations = {};
          
          for (const image of data.images) {
            if (image.id) {
              try {
                const annotationResponse = await fetch(`${API_BASE_URL}annotations.php?image_id=${image.id}`);
                if (annotationResponse.ok) {
                  const annotationData = await annotationResponse.json();
                  if (annotationData.success) {
                    initialAnnotations[image.id] = annotationData.annotations || [];
                  } else {
                    initialAnnotations[image.id] = [];
                  }
                } else {
                  initialAnnotations[image.id] = [];
                }
              } catch (error) {
                initialAnnotations[image.id] = [];
              }
            }
          }
          
          setAllAnnotations(initialAnnotations);
        } catch (error) {
          setAllAnnotations({});
        }
      }
    } catch (error) {
      console.error('Error fetching vehicle:', error);
      setError(error.message);
    } finally {
      setIsLoading(false);
    }
  }, [id]);





  // Save annotations for a specific image
  const saveAnnotations = useCallback(async (imageId, annotations) => {
    try {
      const response = await fetch(`${API_BASE_URL}annotations.php?image_id=${imageId}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ annotations })
      });
      
      if (response.ok) {
        const data = await response.json();
        if (data.success) {
          return true;
        }
      }
    } catch (error) {
      // Could add error handling here (e.g., show toast notification)
    }
    return false;
  }, []);

  // Throttled save function to prevent too many API calls during dragging
  const throttledSaveAnnotations = useCallback((imageId, annotations) => {
    // Store the latest annotations for this image
    pendingUpdates.current[imageId] = annotations;
    
    // Clear any existing timeout for this image
    if (saveTimeouts.current[imageId]) {
      clearTimeout(saveTimeouts.current[imageId]);
    }
    
    // Set a new timeout to save after 500ms of inactivity
    saveTimeouts.current[imageId] = setTimeout(async () => {
      const annotationsToSave = pendingUpdates.current[imageId];
      if (annotationsToSave) {
        await saveAnnotations(imageId, annotationsToSave);
        delete pendingUpdates.current[imageId];
        delete saveTimeouts.current[imageId];
      }
    }, 500); // 500ms throttle delay
  }, [saveAnnotations]);

  // Delete annotations for a specific image
  const deleteAnnotations = useCallback(async (imageId) => {
    try {
      const response = await fetch(`${API_BASE_URL}annotations.php?image_id=${imageId}`, {
        method: 'DELETE'
      });
      
      if (response.ok) {
        const data = await response.json();
        if (data.success) {
          return true;
        }
      }
    } catch (error) {
      // Could add error handling here (e.g., show toast notification)
    }
    return false;
  }, []);

  useEffect(() => {
    fetchVehicle();
  }, [fetchVehicle]);

  // Cleanup throttled saves on unmount
  useEffect(() => {
    return () => {
      // Clear all pending timeouts on component unmount
      Object.values(saveTimeouts.current).forEach(clearTimeout);
      saveTimeouts.current = {};
      pendingUpdates.current = {};
    };
  }, []);

  const handleFileChange = (event) => {
    setSelectedFile(event.target.files[0]);
    setUploadError(null);
    setUploadResult(null);
  };

  const handleUpload = async () => {
    if (!selectedFile) return;

    setIsUploading(true);
    setUploadError(null);
    setUploadResult(null);

    try {
      const formData = new FormData();
      formData.append('file', selectedFile);
      formData.append('vehicleId', id);

      const response = await fetch(API_BASE_URL, {
        method: 'POST',
        body: formData,
      });

      if (!response.ok) {
        throw new Error(`Upload failed: ${response.status}`);
      }

      const result = await response.json();
      setUploadResult(result);
      setSelectedFile(null);
      
      // Refresh vehicle data to get the new image
      await fetchVehicle();
    } catch (error) {
      setUploadError(error.message);
    } finally {
      setIsUploading(false);
    }
  };

  const handleAnnotationsChange = useCallback((imageId, newAnnotations) => {
    // Ensure newAnnotations is always an array
    const validAnnotations = Array.isArray(newAnnotations) ? newAnnotations : [];
    
    // Update local state immediately for responsive UI
    setAllAnnotations(prev => ({
      ...prev,
      [imageId]: validAnnotations
    }));
    
    // Use throttled save to prevent excessive API calls during dragging
    throttledSaveAnnotations(imageId, validAnnotations);
  }, [throttledSaveAnnotations]);

  const clearAllAnnotations = useCallback(async () => {
    if (vehicle && vehicle.images && Array.isArray(vehicle.images)) {
      for (const image of vehicle.images) {
        if (image.id) {
          await deleteAnnotations(image.id);
        }
      }
      setAllAnnotations({});
    }
  }, [vehicle, deleteAnnotations]);



  if (isLoading) {
    return (
      <>
        <Header
          title="Loading Vehicle..."
          subtitle="Please wait"
          showBackButton={true}
          backUrl="/"
        />
        <div className="loading-message">Loading vehicle data...</div>
      </>
    );
  }

  if (error) {
    return (
      <>
        <Header
          title="Error Loading Vehicle"
          subtitle="Something went wrong"
          showBackButton={true}
          backUrl="/"
        />
        <div className="error-message">
          Error: {error}
          <br />
          <Link to="/">Return to Home</Link>
        </div>
      </>
    );
  }

  if (!vehicle) {
    return (
      <>
        <Header
          title="Vehicle Not Found"
          subtitle="The requested vehicle could not be found"
          showBackButton={true}
          backUrl="/"
        />
        <div className="error-message">
          Vehicle not found.
          <br />
          <Link to="/">Return to Home</Link>
        </div>
      </>
    );
  }

  return (
    <>
      <Header
        title={`${vehicle.make} ${vehicle.model} (${vehicle.year})`}
        subtitle={`VIN: ${vehicle.vin} | License: ${vehicle.license_plate}`}
        showBackButton={true}
        backUrl="/"
      />
      <div className="vehicle-page">
        {/* Upload Section */}
        <div className="upload-section">
          <h3>Upload New Image</h3>
          <input type="file" onChange={handleFileChange} accept="image/*" />
          <button onClick={handleUpload} disabled={!selectedFile || isUploading}>
            {isUploading ? 'Uploading...' : 'Upload'}
          </button>
          {uploadResult && (
            <div className="upload-success">Upload successful!</div>
          )}
          {uploadError && (
            <div className="upload-error">Upload failed: {uploadError}</div>
          )}
        </div>

        {/* Controls */}
        {vehicle.images && Array.isArray(vehicle.images) && vehicle.images.length > 0 && (
        <div className="controls">
          <button onClick={() => setIsEditMode(!isEditMode)}>
            {isEditMode ? 'View Mode' : 'Edit Mode'}
          </button>
          {isEditMode && (
            <button onClick={clearAllAnnotations}>Clear All Annotations</button>
          )}
        </div>
        )}

        {/* Images with Annotations */}
        {vehicle.images && Array.isArray(vehicle.images) && vehicle.images.length > 0 ? (
          vehicle.images.map((image, imageIndex) => {
            return (
              <div key={image.id || imageIndex} className="image-section">
                <h4>Image {imageIndex + 1}</h4>
                <AnnotationOverlay
                  imageSrc={typeof image === 'string' ? image : image.fileName || image.url || image.src}
                  annotations={allAnnotations[image.id] || []}
                  onChange={(newAnnotations) => {
                    handleAnnotationsChange(image.id, newAnnotations);
                  }}
                  isEditMode={isEditMode}
                />
                <AnnotationsTable 
                  annotations={allAnnotations[image.id] || []}
                  imageNumber={imageIndex + 1}
                />
              </div>
            );
          })
        ) : (
          <div className="no-images-message">No images available for this vehicle.</div>
        )}


      </div>
    </>
  );
}
