import axios from "axios";
import "../App.css";
export const DownloadFiles = (props :any)=>{
  const calculationWithExpenses = props.files[0];
  const calculationWithoutExpenses = props.files[1]
  const declaration = props.files[2];

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
                <button onClick={()=>download(calculationWithExpenses)} className="button">Калькуляція №1</button>
                <button onClick={()=>download(calculationWithoutExpenses)} className="button">Калькуляція №2</button>
                <button onClick={()=>download(declaration)} className="button">Декларація</button>
              </div>
            </div>
         
)
}