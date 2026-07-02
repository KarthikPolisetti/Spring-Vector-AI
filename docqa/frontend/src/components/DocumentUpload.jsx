import React, { useState, useCallback } from 'react';
import { useDropzone } from 'react-dropzone';
import { uploadDocument } from '../services/api';

const DocumentUpload = ({ onUploadSuccess }) => {
    const [uploading, setUploading] = useState(false);
    const [progress, setProgress] = useState(0);
    const [error, setError] = useState(null);

    const onDrop = useCallback(async (acceptedFiles) => {
        const file = acceptedFiles[0];
        if (!file) return;

        setUploading(true);
        setError(null);
        setProgress(0);

        try {
            const response = await uploadDocument(file, (p) => setProgress(p));
            onUploadSuccess(response.data);
        } catch (err) {
            setError(err.response?.data?.message || err.message || 'Failed to upload document');
        } finally {
            setUploading(false);
        }
    }, [onUploadSuccess]);

    const { getRootProps, getInputProps, isDragActive } = useDropzone({
        onDrop,
        accept: { 'application/pdf': ['.pdf'] },
        multiple: false
    });

    return (
        <div className="p-4 bg-white rounded-lg shadow-md mb-6">
            <div
                {...getRootProps()}
                className={`border-2 border-dashed rounded-lg p-8 text-center cursor-pointer transition-colors
          ${isDragActive ? 'border-blue-500 bg-blue-50' : 'border-gray-300 hover:border-blue-400'}`}
            >
                <input {...getInputProps()} />
                {uploading ? (
                    <div>
                        <p className="text-gray-600 mb-2">Uploading & Processing... {progress}%</p>
                        <div className="w-full bg-gray-200 rounded-full h-2.5">
                            <div className="bg-blue-600 h-2.5 rounded-full transition-all duration-300" style={{ width: `${progress}%` }}></div>
                        </div>
                    </div>
                ) : isDragActive ? (
                    <p className="text-blue-500 font-semibold text-lg">Drop the PDF here...</p>
                ) : (
                    <div>
                        <svg className="mx-auto h-12 w-12 text-gray-400 mb-3" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12" />
                        </svg>
                        <p className="text-gray-600 text-lg">Drag & drop a PDF here, or click to select file</p>
                        {error && <p className="text-red-500 mt-3 text-sm font-semibold">{error}</p>}
                    </div>
                )}
            </div>
        </div>
    );
};

export default DocumentUpload;