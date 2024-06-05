import axios from "axios";
import { BASE_URL } from "../utils/config";
import jwtDecode from "jwt-decode";

const API_URL = BASE_URL + "/api/v1/auth";

interface DecodedToken {
  roles: string[];
  sub: string;
  iat: number;
  exp: number;
}


export const login = async (username: string, password: string) => {
  const response = await axios
    .post(API_URL + "/login", {
      username,
      password,
    });
  if (response.data.token) {
    localStorage.setItem("user", response.data.token);
  }
  return response.data;
};

export const logout = () => {
  localStorage.removeItem("user");
};

export const getCurrentUser = () => {
  const userToken = localStorage.getItem("user");

  if (userToken) {
    const decodedToken : DecodedToken = jwtDecode(userToken);
    return decodedToken.sub;
  }

  return null;
};

