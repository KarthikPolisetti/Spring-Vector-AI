import React, { useState } from 'react';

const SourceChunks = ({ chunks }) => {
    const [isOpen, setIsOpen] = useState(false);

    if (!chunks || chunks.length === 0) return null;

    return (
        <div className="mt-3 pt-3 border-t border-gray-200">
            <button
                onClick={() => setIsOpen(!isOpen)}
                className="text-sm text-blue-600 hover:text-blue-800 font-semibold focus:outline-none flex items-center transition-colors"
            >
                {isOpen ? '▼ Hide Source Text' : '▶ View Source Text'}
            </button>

            {isOpen && (
                <div className="mt-3 space-y-2 border-l-4 border-blue-400 pl-3">
                    {chunks.map((chunk, idx) => (
                        <div key={idx} className="bg-gray-50 p-3 rounded text-gray-700 text-sm italic shadow-sm">
                            "{chunk}"
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
};

export default SourceChunks;