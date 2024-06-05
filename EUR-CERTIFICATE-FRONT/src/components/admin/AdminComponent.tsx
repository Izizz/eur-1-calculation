import axios from 'axios';
import React, { useEffect, useState } from 'react';
import { BASE_URL } from "../../utils/config";

const AdminComponent: React.FC = () => {
    const [users, setUsers] = useState([]);
    const token = localStorage.getItem('user');

   
    const API_URL = BASE_URL + "/api/v1";

    useEffect(() => {

      axios.get(API_URL + '/users/all?pageNumber=0&pageSize=10&sortType=username', 
      {
        headers:{"Authorization": "Bearer " + token}})
        .then(response => {
          setUsers(response.data.content);
        })
        .catch(error => {
          console.error('Error fetching users:', error);
        });
    }, []);
  
    return (
        <div className='users'>
        <div className="container">
        <h1 className="header">User List</h1>
        <table className="table"> 
          <thead>
            <tr>
              <th>ID</th>
              <th>Name</th>
              <th>Roles</th>
            </tr>
          </thead>
          <tbody>
            {users.map((user: any) => (
              <tr key={user.user_id} className="table-row">
                <td>{user.user_id}</td>
                <td>{user.username}</td>
                <td>
                  {user.roles.map((role: any) => (
                    <span key={role.role_id} className="role">{role.rolename} </span>
                  ))}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
    </div>
    </div>
    
    
    );
};

export default AdminComponent;
