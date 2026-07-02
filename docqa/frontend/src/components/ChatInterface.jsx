import React, { useState } from 'react';
import { queryDocument } from '../services/api';
import SourceChunks from './SourceChunks';
import ReactMarkdown from 'react-markdown';

const ChatInterface = ({ documentId }) => {
    const [question, setQuestion] = useState('');
    const [history, setHistory] = useState([]);
    const [loading, setLoading] = useState(false);

    const handleAsk = async (e) => {
        e.preventDefault();
        if (!question.trim()) return;

        const currentQ = question;
        setQuestion('');

        // Add the question to the UI immediately
        const newEntry = { question: currentQ, answer: null, sourceChunks: [] };
        setHistory(prev => [...prev, newEntry]);
        setLoading(true);

        try {
            // Call your Spring Boot backend
            const response = await queryDocument(documentId, currentQ);

            // Update the UI with the AI's answer
            setHistory(prev => {
                const updated = [...prev];
                updated[updated.length - 1] = {
                    question: currentQ,
                    answer: response.data.answer,
                    sourceChunks: response.data.sourceChunks
                };
                return updated;
            });
        } catch (err) {
            setHistory(prev => {
                const updated = [...prev];
                updated[updated.length - 1].answer = "Error: Could not fetch answer. Check console.";
                return updated;
            });
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="flex flex-col h-[600px] bg-white rounded-lg shadow-lg border border-gray-200 mt-6 overflow-hidden">

            {/* Header */}
            <div className="bg-gray-800 text-white p-4 font-semibold">
                Document Q&A Session
            </div>

            {/* Chat History Area */}
            <div className="flex-1 overflow-y-auto p-4 space-y-6 bg-gray-50">
                {history.length === 0 && !loading && (
                    <div className="text-center text-gray-500 mt-10">
                        Ask a question about your uploaded document to get started!
                    </div>
                )}

                {history.map((chat, idx) => (
                    <div key={idx} className="space-y-3">
                        {/* User Question Bubble */}
                        <div className="flex justify-end">
                            <div className="bg-blue-600 text-white rounded-lg rounded-tr-none py-2 px-4 max-w-[80%] shadow">
                                {chat.question}
                            </div>
                        </div>

                        {/* AI Answer Bubble */}
                        {chat.answer && (
                            <div className="flex justify-start">
                                <div className="bg-white text-gray-800 rounded-lg rounded-tl-none py-3 px-5 max-w-[90%] shadow border border-gray-200">
                                    <div className="prose prose-sm max-w-none">
                                        <ReactMarkdown>
                                            {chat.answer}
                                        </ReactMarkdown>
                                    </div>
                                    <SourceChunks chunks={chat.sourceChunks} />
                                </div>
                            </div>
                        )}
                    </div>
                ))}

                {/* Loading Spinner Indicator */}
                {loading && (
                    <div className="flex justify-start">
                        <div className="bg-white text-gray-500 rounded-lg py-3 px-5 shadow border border-gray-200 flex items-center gap-2">
                            <div className="w-2 h-2 bg-blue-500 rounded-full animate-bounce"></div>
                            <div className="w-2 h-2 bg-blue-500 rounded-full animate-bounce delay-100"></div>
                            <div className="w-2 h-2 bg-blue-500 rounded-full animate-bounce delay-200"></div>
                            <span className="ml-2 text-sm italic">The AI is reading the document...</span>
                        </div>
                    </div>
                )}
            </div>

            {/* Input Form */}
            <form onSubmit={handleAsk} className="p-4 bg-white border-t border-gray-200 flex gap-3">
                <input
                    type="text"
                    value={question}
                    onChange={(e) => setQuestion(e.target.value)}
                    placeholder="Ask something specific..."
                    className="flex-1 rounded-lg border border-gray-300 p-3 focus:outline-none focus:ring-2 focus:ring-blue-500 shadow-sm"
                    disabled={loading}
                />
                <button
                    type="submit"
                    disabled={loading || !question.trim()}
                    className="bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-6 rounded-lg disabled:bg-blue-300 transition-colors shadow-sm"
                >
                    Send
                </button>
            </form>
        </div>
    );
};

export default ChatInterface;