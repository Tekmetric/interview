import React, { useState, useRef, useCallback } from 'react';
import './AnnotationOverlay.scss';

const CATEGORIES = ['red', 'yellow', 'green'];

export default function AnnotationOverlay({ imageSrc, annotations = [], onChange, isEditMode }) {
  
  const [selectedAnnotation, setSelectedAnnotation] = useState(null);
  const [editingLabel, setEditingLabel] = useState(null);
  const [dragState, setDragState] = useState(null);
  const [resizeState, setResizeState] = useState(null);
  
  const imageRef = useRef(null);
  const containerRef = useRef(null);

  // Calculate bounds on-demand instead of using useMemo
  const getBounds = useCallback(() => {
    if (!containerRef.current) return { width: 0, height: 0 };
    const rect = containerRef.current.getBoundingClientRect();
    return { width: rect.width, height: rect.height };
  }, []);

  const handlePointerDown = useCallback((event, annotation = null) => {
    if (!isEditMode) return;
    
    // Prevent default touch behavior to avoid scrolling
    if (event.touches) {
      event.preventDefault();
    }
    
    const bounds = getBounds();
    
    // Ensure bounds are valid before proceeding
    if (!bounds.width || !bounds.height || bounds.width <= 0 || bounds.height <= 0) {
      return;
    }
    
    const rect = containerRef.current.getBoundingClientRect();
    // Handle both mouse and touch events
    const clientX = event.clientX || event.touches?.[0]?.clientX;
    const clientY = event.clientY || event.touches?.[0]?.clientY;
    const x = (clientX - rect.left) / bounds.width;
    const y = (clientY - rect.top) / bounds.height;
    

    
      if (annotation) {
        // Clicking on existing annotation
        setSelectedAnnotation(annotation);
        
        // Check if clicking on resize handle - make it larger and more precise
        const handleSize = 0.08; // Increased from 0.05
        const isOnResizeHandle = 
          Math.abs(x - (annotation.x + annotation.width)) < handleSize &&
          Math.abs(y - (annotation.y + annotation.height)) < handleSize;
        
        if (isOnResizeHandle) {
          setResizeState({ annotation, startX: x, startY: y });
        } else {
          setDragState({ annotation, startX: x, startY: y });
        }
      } else {
      // Creating new annotation - fixed 60x60px square on click
      const sizePx = 60;
      
      // Ensure bounds are valid before calculating
      if (bounds.width <= 0 || bounds.height <= 0) {
        return; // Don't create annotation if bounds are invalid
      }
      
      const normalizedWidth = sizePx / bounds.width;
      const normalizedHeight = sizePx / bounds.height;
      
      const newAnnotation = {
        id: Date.now(),
        x: Math.max(0, x - normalizedWidth / 2),
        y: Math.max(0, y - normalizedHeight / 2),
        width: normalizedWidth,
        height: normalizedHeight,
        label: `Annotation ${annotations.length + 1}`,
        category: 'red'
      };
      
      const newArray = [...(Array.isArray(annotations) ? annotations : []), newAnnotation];
      onChange(newArray);
      
      setSelectedAnnotation(newAnnotation);
      setEditingLabel(newAnnotation);
    }
  }, [isEditMode, annotations, onChange, getBounds]);

  const handlePointerMove = useCallback((event) => {
    if (!isEditMode) return;
    
    // Prevent default touch behavior during drag/resize to avoid scrolling
    if (event.touches && (dragState || resizeState)) {
      event.preventDefault();
    }
    
    if (dragState) {
      const bounds = getBounds();
      const rect = containerRef.current.getBoundingClientRect();
      // Handle both mouse and touch events
      const clientX = event.clientX || event.touches?.[0]?.clientX;
      const clientY = event.clientY || event.touches?.[0]?.clientY;
      const x = (clientX - rect.left) / bounds.width;
      const y = (clientY - rect.top) / bounds.height;
      
      const deltaX = x - dragState.startX;
      const deltaY = y - dragState.startY;
      
      const updatedAnnotations = (Array.isArray(annotations) ? annotations : []).map(ann => 
        ann.id === dragState.annotation.id
          ? {
              ...ann,
              x: Math.max(0, Math.min(1 - ann.width, ann.x + deltaX)),
              y: Math.max(0, Math.min(1 - ann.height, ann.y + deltaY))
            }
          : ann
      );
      onChange(updatedAnnotations);
      setDragState({ ...dragState, startX: x, startY: y });
    }
    
    if (resizeState) {
      const bounds = getBounds();
      const rect = containerRef.current.getBoundingClientRect();
      // Handle both mouse and touch events
      const clientX = event.clientX || event.touches?.[0]?.clientX;
      const clientY = event.clientY || event.touches?.[0]?.clientY;
      const x = (clientX - rect.left) / bounds.width;
      const y = (clientY - rect.top) / bounds.height;
      
      const deltaX = x - resizeState.startX;
      const deltaY = y - resizeState.startY;
      
      const resizedAnnotations = (Array.isArray(annotations) ? annotations : []).map(ann => 
        ann.id === resizeState.annotation.id
          ? {
              ...ann,
              width: Math.max(0.01, ann.width + deltaX),
              height: Math.max(0.01, ann.height + deltaY)
            }
          : ann
      );
      onChange(resizedAnnotations);
      setResizeState({ ...resizeState, startX: x, startY: y });
    }
  }, [isEditMode, dragState, resizeState, annotations, onChange, getBounds]);

  const handleMouseUp = useCallback(() => {
    setDragState(null);
    setResizeState(null);
  }, []);

  const handleLabelChange = useCallback((annotationId, newLabel) => {
    setEditingLabel(prev => prev?.id === annotationId ? { ...prev, label: newLabel } : prev);
  }, []);

  const commitLabel = useCallback((annotationId) => {
    const labeledAnnotations = (Array.isArray(annotations) ? annotations : []).map(ann => 
      ann.id === annotationId
        ? { ...ann, label: editingLabel?.label || ann.label }
        : ann
    );
    onChange(labeledAnnotations);
    setEditingLabel(null);
  }, [annotations, editingLabel, onChange]);

  const removeAnnotation = useCallback((annotationId) => {
    const filteredAnnotations = (Array.isArray(annotations) ? annotations : []).filter(ann => ann.id !== annotationId);
    onChange(filteredAnnotations);
    setSelectedAnnotation(null);
    setEditingLabel(null);
    setDragState(null);
    setResizeState(null);
  }, [annotations, onChange]);

  const updateCategory = useCallback((annotationId, category) => {
    const categorizedAnnotations = (Array.isArray(annotations) ? annotations : []).map(ann => 
      ann.id === annotationId
        ? { ...ann, category }
        : ann
    );
    onChange(categorizedAnnotations);
  }, [annotations, onChange]);

  const boxStyleFromRect = (rect) => ({
    position: 'absolute',
    left: `${rect.x * 100}%`,
    top: `${rect.y * 100}%`,
    width: `${rect.width * 100}%`,
    height: `${rect.height * 100}%`,
    cursor: isEditMode ? 'pointer' : 'default'
  });

  const isSelected = (annotation) => selectedAnnotation?.id === annotation.id;
  const isEditing = (annotation) => editingLabel?.id === annotation.id;

  return (
    <div 
      className="annotation-overlay"
      ref={containerRef}
      onMouseMove={handlePointerMove}
      onMouseUp={handleMouseUp}
      onTouchMove={(e) => {
        // Prevent scrolling during drag/resize operations
        if (dragState || resizeState) {
          e.preventDefault();
        }
        handlePointerMove(e);
      }}
      onTouchEnd={handleMouseUp}
      onTouchStart={(e) => {
        // Prevent default touch behavior
        e.preventDefault();
      }}
    >
      <img 
        ref={imageRef}
        src={imageSrc} 
        alt="Vehicle"
        className="annotation-image"
        draggable={false}
      />
      
      {annotations.filter(annotation => 
        annotation.x !== null && 
        annotation.y !== null && 
        annotation.width !== null && 
        annotation.height !== null &&
        !isNaN(annotation.x) && 
        !isNaN(annotation.y) && 
        !isNaN(annotation.width) && 
        !isNaN(annotation.height) &&
        annotation.width > 0 && 
        annotation.height > 0
      ).map((annotation) => {
        return (
          <div
            key={annotation.id}
            className={`annotation-box category-${annotation.category} ${isSelected(annotation) ? 'selected' : ''}`}
            style={boxStyleFromRect(annotation)}
            onMouseDown={(e) => handlePointerDown(e, annotation)}
            onTouchStart={(e) => {
              e.preventDefault();
              handlePointerDown(e, annotation);
            }}
          >
          <div className="annotation-label">
            {isEditing(annotation) ? (
              <input
                type="text"
                value={editingLabel?.label || ''}
                onChange={(e) => handleLabelChange(annotation.id, e.target.value)}
                onBlur={() => commitLabel(annotation.id)}
                onKeyDown={(e) => {
                  if (e.key === 'Enter') {
                    commitLabel(annotation.id);
                  }
                }}
                onMouseDown={(e) => {
                  e.stopPropagation();
                }}
                onClick={(e) => e.stopPropagation()}
              />
            ) : (
              <span 
                onMouseDown={(e) => {
                  e.stopPropagation();
                }}
                onClick={(e) => {
                  e.stopPropagation();
                  setEditingLabel(annotation);
                }}
                onTouchStart={(e) => {
                  e.preventDefault();
                  e.stopPropagation();
                }}
                onTouchEnd={(e) => {
                  e.preventDefault();
                  e.stopPropagation();
                  setEditingLabel(annotation);
                }}
              >
                {annotation.label}
              </span>
            )}
            
            <div className="category-selector">
              {CATEGORIES.map((category) => (
                                  <button
                    key={category}
                    className={`category-option category-${category}`}
                    onMouseDown={(e) => {
                      e.stopPropagation();
                    }}
                    onClick={(e) => {
                      e.stopPropagation();
                      updateCategory(annotation.id, category);
                    }}
                    onTouchStart={(e) => {
                      e.preventDefault();
                      e.stopPropagation();
                    }}
                    onTouchEnd={(e) => {
                      e.preventDefault();
                      e.stopPropagation();
                      updateCategory(annotation.id, category);
                    }}
                    title={category}
                  />
              ))}
            </div>
          </div>
          
          {isEditMode && (
            <button
              className="delete-annotation"
              onMouseDown={(e) => {
                e.stopPropagation();
              }}
              onClick={(e) => {
                e.stopPropagation();
                removeAnnotation(annotation.id);
              }}
              onTouchStart={(e) => {
                e.preventDefault();
                e.stopPropagation();
              }}
              onTouchEnd={(e) => {
                e.preventDefault();
                e.stopPropagation();
                removeAnnotation(annotation.id);
              }}
              title="Delete annotation"
            >
              ×
            </button>
          )}
          
          {isEditMode && (
            <div 
              className="resize-handle"
              onMouseDown={(e) => {
                e.stopPropagation();
                const bounds = getBounds();
                const rect = containerRef.current.getBoundingClientRect();
                // Handle both mouse and touch events
                const clientX = e.clientX || e.touches?.[0]?.clientX;
                const clientY = e.clientY || e.touches?.[0]?.clientY;
                const x = (clientX - rect.left) / bounds.width;
                const y = (clientY - rect.top) / bounds.height;
                setResizeState({ annotation, startX: x, startY: y });
              }}
              onTouchStart={(e) => {
                e.preventDefault();
                e.stopPropagation();
                const bounds = getBounds();
                const rect = containerRef.current.getBoundingClientRect();
                // Handle touch events
                const clientX = e.touches[0].clientX;
                const clientY = e.touches[0].clientY;
                const x = (clientX - rect.left) / bounds.width;
                const y = (clientY - rect.top) / bounds.height;
                setResizeState({ annotation, startX: x, startY: y });
              }}
            />
          )}
        </div>
      );
      })}
      
      {isEditMode && (
        <div
          className="image-overlay"
          data-testid="image-overlay"
          onMouseDown={handlePointerDown}
          onTouchStart={(e) => {
            e.preventDefault();
            handlePointerDown(e);
          }}
          style={{ 
            pointerEvents: 'auto',
            zIndex: 1 
          }}
        />
      )}
    </div>
  );
}
