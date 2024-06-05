import React, { useEffect, useState } from 'react';
import "./fileUpload.css";
import axios from 'axios';
import { BASE_URL } from '../../utils/config';
import { useNavigate } from 'react-router-dom';
import { toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

const FileUpload: React.FC = () => {

    const user = window.localStorage.getItem('user');
    const API_URL = BASE_URL + "/api/v1/files/upload";

    const [selectedFiles, setSelectedFiles] = useState<File[]>([]);
    const [isDragActive, setIsDragActive] = useState(false);
    const [types, setTypes] = useState<string[]>([]);
    const [isLoading, setLoading] = useState(false);

    const navigateTo = useNavigate();

    useEffect(()=>{
      if(!window.localStorage.getItem("user")){
         navigateTo("/login");
      }
    
    });

    const handleTypeChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
        setTypes([event.target.value]);
    };
    const handleFileInputChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        if (event.target.files) {
            const filesArray = Array.from(event.target.files);
            setSelectedFiles([...selectedFiles, ...filesArray]);
        }
    };

    const handleDrop = (event: React.DragEvent<HTMLElement>) => {
        event.preventDefault();
        if (event.dataTransfer.files) {
            const files = Array.from(event.dataTransfer.files);
            setSelectedFiles([...selectedFiles, ...files]);
            setIsDragActive(false);
        }
    };

    const handleDragOver = (event: React.DragEvent<HTMLElement>) => {
        event.preventDefault();
        setIsDragActive(true);
    };

    const handleDragLeave = (event: React.DragEvent<HTMLElement>) => {
        event.preventDefault();
        setIsDragActive(false);
    };
    const handleFileRemove = (index: number) => {
        const updatedFiles = [...selectedFiles];
        updatedFiles.splice(index, 1);
        setSelectedFiles(updatedFiles);
    };

    const handleUpload = () => {
        setLoading(true); // Set loading state to true

        const formData = new FormData();
        selectedFiles.forEach((file) => {
            formData.append('files', file);
        });
        types.forEach((type) => {
            formData.append('types', type);
        });

        axios
            .post(API_URL, formData, {
                headers: {
                    'Content-Type': 'multipart/form-data',
                    'Authorization': 'Bearer ' + user,
                },
            })
            .then((response) => {
                toast.success('Files uploaded successfully!');
                setSelectedFiles([]);
                setTypes([]);
            })
            .catch((error) => {
                toast.error('Files were not uploaded');
                console.error(error);
            })
            .finally(() => {
                setLoading(false); // Set loading state to false
            });
    };

    return (
        <div className='upload-container'>
            <div className="file-upload">
                <div className='file-type-select'>
                    <select className="file-type-select" onChange={handleTypeChange}>
                        <option disabled>Select file type</option>
                        <option value="DECLARATION_EXCEL">Declaration Excel</option>
                        <option value="DECLARATION_PDF">Declaration Pdf</option>
                        <option value="TYPE_INVOICE_PDF">Invoice</option>
                        <option value="INCOME_FILE">Income </option>
                    </select>
                </div>
                <label
                    className={`dropzone ${isDragActive ? 'active' : ''}`}
                    onDrop={handleDrop}
                    onDragOver={handleDragOver}
                    onDragLeave={handleDragLeave}
                    htmlFor="file-input"
                >
                    <input
                        type="file"
                        id="file-input"
                        className="file-input"
                        multiple
                        onChange={handleFileInputChange}
                    />
                    <p>{isDragActive ? 'Release to drop' : 'Drag file here or click to select files'}</p>
                </label>
                {selectedFiles.length > 0 && (
                    <div className="preview">
                        {selectedFiles.map((file, index) => (
                            <div key={index} className="preview-image">
                                <img className='img-text' src={URL.createObjectURL(file)} alt={file.name} />
                                <div className="remove-button" onClick={() => handleFileRemove(index)}>X</div>
                            </div>
                        ))}
                    </div>
                )}
                {selectedFiles.length > 0 && (
                    <>
                        {isLoading ? (
                            <div className="loading-indicator">
                                <div className="loading-circle"></div>
                            </div>
                        ) : (
                            <button className="upload-button" onClick={handleUpload}>Upload</button>
                        )}
                    </>
                )}
            </div>
        </div>
    );
};

export default FileUpload;
