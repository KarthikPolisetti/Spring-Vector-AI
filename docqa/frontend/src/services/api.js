import axios from 'axios';

// This points directly to your Spring Boot backend!
const BASE_URL = 'http://localhost:8080/api';

export const uploadDocument = (file, onProgress) => {
    const formData = new FormData();
    formData.append('file', file);

    return axios.post(`${BASE_URL}/documents/upload`, formData, {
        onUploadProgress: (e) => {
            onProgress(Math.round((e.loaded * 100) / e.total));
        }
    });
};

export const getDocuments = () => axios.get(`${BASE_URL}/documents`);

export const queryDocument = (documentId, question) =>
    axios.post(`${BASE_URL}/documents/${documentId}/query`, { question });

export const getQueryHistory = (documentId) =>
    axios.get(`${BASE_URL}/documents/${documentId}/history`);