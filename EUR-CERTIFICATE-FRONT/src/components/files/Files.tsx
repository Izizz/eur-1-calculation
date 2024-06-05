import React, { useState, useEffect } from 'react';
import axios from 'axios';
import "../../App.css";
import 'bootstrap/dist/css/bootstrap.min.css';
import { BASE_URL } from "../../utils/config";
import "./files.css";
import { useNavigate } from 'react-router-dom';


type FileData = {
  fileName: string;
  fileDownloadUri: string;
  fileType: string;
  size: number;
};

const FilesTable: React.FC = () => {

    const API_URL = BASE_URL + "/api/v1";

    const token = window.localStorage.getItem("user"); 
    const [files, setFiles] = useState<FileData[]>([]);
    const [search,setSearch] = useState("");
    const [isEmpty, setIsEmpty] = useState(false);
    const [isLoading, setLoading] = useState(false);

    function convertBytesToKB(bytes: number): number {
    const kilobytes = bytes / 1024;
    return kilobytes;
  }

  const navigateTo = useNavigate();

    useEffect(()=>{
      if(!window.localStorage.getItem("user")){
         navigateTo("/login");
      }
      
      fetchFiles(search);
    },[]);

  const fetchFiles = (searchTitle : string) =>{
    const token = localStorage.getItem('user');
    setLoading(true);
    setIsEmpty(false)
    axios
      .get(API_URL + `/files/all?page=0&size=10&search=${searchTitle}&searchBy=BY_STRING`, 
      {
        headers:{"Authorization": "Bearer " + token}})
      .then((response) => {
        
        if(response.data.length === 0){
          setIsEmpty(true);
        }
        
        setFiles(response.data);
      })
      .catch((error) => {
        console.error(error);
      }).finally(() => {
        setLoading(false); // Set loading state to false
    });
  }

  const handleInputChange = (event:any) => {
    setSearch(event.target.value);
  };

  const onSubmit = (event :any) => {
    event.preventDefault();
    setSearch(event.target.value);
    fetchFiles(search);
    };

  const handleTypeSearch= (type : String)=>{
    const token = localStorage.getItem('user');
    setIsEmpty(false)
    axios
      .get(API_URL + `/files/all?page=0&size=10&search=${type}&searchBy=BY_TYPE`, 
      {
        headers:{"Authorization": "Bearer " + token}})
      .then((response) => {
        
        if(response.data.length === 0){
          setIsEmpty(true);
        }
        setFiles(response.data.content);
      })
      .catch((error) => {
        console.error(error);
      });
  }

  const handleFileDownload = (fileName : string)=>{
    axios.get(API_URL + `/files/download/${fileName}`, {
        headers:{
            "Authorization" : "Bearer " + token,
        },
        responseType: "blob",
    }).then((response)=>{
        const href = URL.createObjectURL(response.data);
        const link = document.createElement('a');
        link.href = href;
        link.setAttribute('download',fileName);
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        URL.revokeObjectURL(href);
      })

  }

  const handleEmptyStringSearch = ()=>{
    const token = localStorage.getItem('user');
    setIsEmpty(false);
    axios
      .get(API_URL + `/files/all?page=0&size=10&seacrhBy=BY_EMPTY_STRING`, 
      {
        headers:{"Authorization": "Bearer " + token}})
      .then((response) => {
        
        if(response.data.length === 0){
          setIsEmpty(true);
        }
        setFiles(response.data.content);
      })
      .catch((error) => {
        console.error(error);
      });
  }

  const handleDownloadAll = ()=>{
    axios.get(API_URL + `/files/download/all`, {
        headers:{
            "Authorization" : "Bearer " + token,
        },
        responseType: "blob",
    }).then((response)=>{
        const href = URL.createObjectURL(response.data);
        const link = document.createElement('a');
        link.href = href;
        
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        URL.revokeObjectURL(href);
      })

  }

  function handleFileDelete(fileName: string) {
    const config = {
      headers: { Authorization: `Bearer ${token}` },
    };
    if (window.confirm(`Are you sure you want to delete ${fileName}?`)) {
        axios
        .delete( API_URL + `/files/delete/${fileName}`, config)
        .then((response) => {
            setFiles((prevFiles) =>
            prevFiles.filter((file) => file.fileName !== fileName)
            );
        })
        .catch((error) => {
            console.error(error);
        });
    }
  }

  return (
    <div className='files-container'>
      
      <div className='search'>
        <form onSubmit={onSubmit}>
          <input
            type="text"
            onChange={handleInputChange}
            placeholder="Search"
          />
          <button type="submit">Submit</button>
        </form>
      </div>
      
      <br/>
      {isLoading ? (
                            <div className="loading-indicator">
                                <div className="loading-circle"></div>
                            </div>
                        ) : (
                          <>{isEmpty ? <div><h1>No records</h1></div> : 
                          <section className="intro">
                          <div className="bg-image h-100" >
                            <div className="mask d-flex align-items-center h-100">
                              <div className="container">
                                <div className="row justify-content-center">
                                  <div className="col-12">
                                    <div className="card shadow-2-strong" >
                                      <div className="card-body">
                                        <div className="table-responsive">
                                          <table className="table table-borderless mb-0">
                                            <thead>
                                              <tr>
                                                <th>File Name</th>
                                                <th>Size</th> 
                                                <th>Download</th>
                                                <th>Delete</th>
                                              </tr>
                                            </thead>
                                            <tbody>
                                              {files.map((file) => (
                                                <tr key={file.fileName}>
                                                  <td>{file.fileName}</td>
                                                  <td>{convertBytesToKB(file.size).toFixed(2)} KB</td>
                                                  <td>
                                                  <button
                                                    className="download-button"
                                                    onClick={() => handleFileDownload(file.fileName)}>
                                                    Download
                                                  </button>
                                                  </td>
                                                  <td>
                                                  <button className="delete-button " onClick={() => handleFileDelete(file.fileName)}>
                                                  Delete
                                                  </button>
                                                  </td>
                                                  
                                                </tr>
                                              ))}
                                            </tbody>
                                          </table>
                                         
                                        </div>
                                      </div>
                                    </div>
                                  </div>
                                </div>
                              </div>
                            </div>
                          </div>
                        </section>
                          }</>
                          
                        )}
     
     
      </div>
  );
  };
     

export default FilesTable;
