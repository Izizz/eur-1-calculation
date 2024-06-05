
import { useState} from 'react';
import axios from 'axios';
import '../../App.css';
import { toast} from 'react-toastify';
import { Formik, Field, Form, ErrorMessage } from "formik";
import { BASE_URL } from "../../utils/config";
import { DownloadConvertedInvoice } from './DownloadConvertedInvoice';
import './conversion.css';

export const Conversion=()=>{

  const API_URL = BASE_URL + "/api/v2/convert";
  const API_URL_EXAMPLE_DOWNLOAD = BASE_URL+ "/api/v1/files/download";

  const [invoice,setInvcoice] = useState([]);
  const [deliveryNote,setDeliveryNote] = useState([]);

  const [filesToDownload,setFilesToDownload] = useState([]);

  const [loading, setLoading] = useState<boolean>(false);
  const [isDone,setIsDone] = useState(false);

  const token = window.localStorage.getItem('user');
  

  const onInputChangeForInvoice = (e:any) => {
    setInvcoice(e.target.files)
  };

 const onInputChangeForDeliveryNote = (e:any) => {
    setDeliveryNote(e.target.files)
  };
 
const initialValues: {
    title: string;
  } = {
    title: "",
  };

const onSubmit = (formValue:{title:string}) => {
    const ProductTitle = formValue.title;
    
    const data = new FormData();
    
    setLoading(true);

    data.append('invoice',invoice[0]);
    data.append('delivery note',deliveryNote[0]);
    
    axios.post(API_URL+ '/pdf-excel', data,{
      headers:{
        "Authorization": "Bearer " + token,
      }
    }).then((res) => {
            toast.success('Convertion Success!!');
            setIsDone(true);
            setLoading(false);
            const arr : any = [res.data[0].fileDownloadUri];
            setFilesToDownload(arr);
        }).catch((error) => {
          if (error.response && error.response.data && error.response.data.errorMessages) {
            const errorMessages = error.response.data.errorMessages;
            errorMessages.forEach((errorMessage: string) => {
              toast.error(errorMessage, {
                toastId: errorMessage,
                autoClose: false,
              });
            });
          } else {
            toast.error('Conversion Error');
          }
          setLoading(false);
        });  
};

return(
    <div className='conversion-container'>
  <section className="text-center">
    <div className="card-body py-5 px-md-5">
      <div className="row d-flex justify-content-center">
        <div className="col-lg-8">

          <div>
         
          <Formik initialValues={initialValues}
                  onSubmit={onSubmit} >
            <Form >
              <div className="form-outline mb-4" >
              <div className="login-header">
                <h3 className="fw-bold mb-5 " >Invoice</h3>
              </div>
                
                <input type="file" 
                       className="form-control" 
                       onChange={onInputChangeForInvoice}
                       />        
              </div>

              <div className="login-header">
                <h3 className="fw-bold mb-5 ">Delivery Note</h3>

              </div>
         
              <div className="form-outline mb-4" >
                <input type="file" 
                       className="form-control" 
                       onChange={onInputChangeForDeliveryNote}
                       />        
              </div>

              <div className="form-group ">
            
                    {loading ? (
                      <button type="submit" className="btn btn-primary btn-block" disabled>
                        <div className="spinner-border text-light" role="status">
                          <span className="visually-hidden">Loading...</span>
                        </div>
                      </button>
                    ) : (
                      <button type="submit" className="btn btn-primary btn-block">
                        <span>Submit</span>
                      </button>
                    )}
              </div>

            </Form>
          </Formik>
          
          </div>
          {isDone &&
          <DownloadConvertedInvoice files={filesToDownload} /> 
        }
        </div>
        </div>
      </div>
  </section>
  </div>
  )
}
