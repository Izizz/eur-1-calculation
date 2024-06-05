import axios from "axios";
import { BASE_URL } from "../utils/config";

export const Upload = (props :any)=>{ 
    const token = window.localStorage.getItem('user');

    const URL = BASE_URL + "/api/v1/files/upload";
  
    const files = props.filesToUpload;
    const upload =()=>{
        console.log("this is files " + files[2].name );
        
            const data = new FormData();
            data.append('files' , files);
            console.log(data.get('files'));
            axios.post(URL, data ,{
                headers:{
                    "Authorization": "Bearer " + token,
                }
            })
    }

    return( <div className="form-group">
    <button onClick={()=>upload()} className="btn btn-primary btn-block" >
      <span>Upload</span>
    </button>
  </div>)
}