import React, { useState } from 'react';
import DocumentUpload from './components/DocumentUpload';
import ChatInterface from './components/ChatInterface';

function App() {
    const [currentDocument, setCurrentDocument] = useState(null);

    const handleUploadSuccess = (docData) => {
        // docData usually comes back from the backend as the raw ID or an object.
        // If your backend returns the raw ID, we use it directly.
        // If it returns an object like { id: 1 }, we use docData.id.
        const docId = typeof docData === 'object' ? docData.id : docData;
        setCurrentDocument(docId);
    };

    return (
        <div className="min-h-screen bg-gray-100 p-8">
            <div className="max-w-4xl mx-auto">

                <div className="text-center mb-8">
                    <h1 className="text-4xl font-extrabold text-gray-900 tracking-tight">
                        RAG Document Assistant
                    </h1>
                    <p className="text-gray-500 mt-2">Upload a PDF and ask questions using AI.</p>
                </div>

                {/* Only show the upload box if no document is currently selected */}
                {!currentDocument ? (
                    <DocumentUpload onUploadSuccess={handleUploadSuccess} />
                ) : (
                    <div className="flex justify-between items-center mb-4 bg-white p-4 rounded-lg shadow-sm border border-gray-200">
                        <p className="text-green-600 font-bold flex items-center gap-2">
                            <span>✅</span> Document Processed Successfully
                        </p>
                        <button
                            onClick={() => setCurrentDocument(null)}
                            className="text-sm text-gray-500 hover:text-red-500 underline"
                        >
                            Upload a different document
                        </button>
                    </div>
                )}

                {/* The Chat Interface */}
                {currentDocument && <ChatInterface documentId={currentDocument} />}

            </div>
        </div>
    );
}

export default App;