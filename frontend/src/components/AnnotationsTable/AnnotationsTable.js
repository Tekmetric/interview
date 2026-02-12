import React from 'react';
import './AnnotationsTable.scss';

export default function AnnotationsTable({ annotations = [], imageNumber, showHeader = true }) {
  if (!Array.isArray(annotations) || annotations.length === 0) {
    return null;
  }

  return (
    <div className="annotations-table-container">
      {showHeader && (
        <h4>Image {imageNumber} Annotations ({annotations.length})</h4>
      )}
      <table className="annotations-table">
        <thead>
          <tr>
            <th>#</th>
            <th>Label</th>
            <th>Category</th>
          </tr>
        </thead>
        <tbody>
          {annotations.map((annotation, index) => (
            <tr key={`${imageNumber}-${index}-${annotation.id}`}>
              <td>{index + 1}</td>
              <td>{annotation.label}</td>
              <td>
                <span className={`category-${annotation.category}`}>
                  {annotation.category}
                </span>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
