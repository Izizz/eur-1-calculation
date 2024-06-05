import axios from "axios";
import "../../App.css";

export const DownloadConvertedInvoice = (props :any)=>{
  const invocie = props.files[0];


    const download=(downloadurl: string )=>{
        

            const token = window.localStorage.getItem('user');
            axios({
                url: downloadurl,
                method:'GET',
                responseType:'blob',
                headers:{
                    "Authorization": "Bearer " + token 
                }
            }).then((response)=>{
                const href = URL.createObjectURL(response.data);
                const fileName : string  = downloadurl.split('/').pop()!;
    
                const link = document.createElement('a');
                link.href = href;
                link.setAttribute('download',fileName);
                document.body.appendChild(link);
                link.click();
                document.body.removeChild(link);
                URL.revokeObjectURL(href);
              })
    }
  return(
            <div >
            
            <br/>
              <div className="button-group">
                <button onClick={()=>download(invocie)} className="button">Download</button>
                
              </div>
            </div>
         
)
}