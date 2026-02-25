import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api', // Verifique se sua API está nessa porta
});

export default api;