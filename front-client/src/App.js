import "./App.css";
import api from "./api/axiosConfig";
import { useState, useEffect } from "react";
import Layout from "./components/Layout";
import { Route, Routes } from "react-router-dom";
import Home from "./components/home/Home";

function App() {
  const [movies, setMovies] = useState();

  const getMovies = async () => {
    try {
      const response = await api.get("/api/v1/movies");
      setMovies(response.data);
    } catch (error) {
      console.log(error);
    }
  };

  useEffect(() => {
    getMovies();
  }, []);

  if (!movies) {
    return (
      <div className="backend-not-up">
        <h2>hmm! It seems the backend API's not working!</h2>
      </div>
    );
  }

  return (
    <div className="App">
      <Routes>
        <Route path="/" element={<Layout />}>
          <Route path="/" element={<Home movies={movies} />}></Route>
        </Route>
      </Routes>
    </div>
  );
}

export default App;
