import { useState } from "react";
import { Upload } from "./Upload";

export const UploadFiles = (props : any) => {
    const [files,setFiles] = useState([]);
    const onInputChange = (e:any) => {
        setFiles(e.target.files);
    };

    
   
    return(
        <div >
        <form method="post" action="#" id="#" >
           <div >
             <label>Upload Your File </label>
             <input type="file" 
                    className="form-control" 
                    onChange={onInputChange}
                    multiple={true}/>
             
           </div>
           
       </form>
       <Upload filesToUpload={files}/>

       </div>
    )
}