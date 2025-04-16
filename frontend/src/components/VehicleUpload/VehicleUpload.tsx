import type { FC } from 'react';
import { useState } from 'react';
import { useField } from 'formik';

interface FileUploadProps {
  name: string;
  isDisabled: boolean;
  fileRef: React.RefObject<HTMLInputElement | null>;
  onPreviewImageChange: (urlString: string | null) => void;
  hasClearedImage: (cleared: boolean) => void;
}

const FileUpload: FC<FileUploadProps> = ({
  name,
  isDisabled,
  fileRef,
  onPreviewImageChange,
  hasClearedImage,
}) => {
  const [_, meta, helpers] = useField(name);
  const [fileName, setFileName] = useState('');

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const files = e.target.files;

    if (files && files.length > 0) {
      const selectedFile = files[0];
      setFileName(selectedFile.name);

      const previewUrl = URL.createObjectURL(selectedFile);
      onPreviewImageChange(previewUrl);
      hasClearedImage(false);

      helpers.setValue(files);
    } else {
      setFileName('');
      helpers.setValue(undefined);
    }
  };

  return (
    <div>
      <label htmlFor="image">Upload Image</label>
      <input
        ref={fileRef}
        type="file"
        name={name}
        id="image"
        onChange={handleChange}
        disabled={isDisabled}
        accept="image/*"
      />
      <p>{fileName || 'No Image Uploaded'}</p>

      {meta.touched && meta.error && <div style={{ color: 'red' }}>{meta.error as string}</div>}
    </div>
  );
};

export default FileUpload;
