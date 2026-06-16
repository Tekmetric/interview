import React, { Component } from "react";
import { Search } from "./components/Search";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { LikedSongs } from "./components/LikedSongs";
import { motion } from "motion/react";
//import logo from './logo.svg';

const queryClient = new QueryClient();

class App extends Component {
  render() {
    return (
      <QueryClientProvider client={queryClient}>
        <div className="min-h-screen bg-onyx p-8">
          <div className="text-center mb-6">
            <motion.h1
              className="text-2xl font-bold text-alabaster inline-block"
              initial={{ width: 0 }}
              animate={{ width: "auto" }}
              transition={{ duration: 2, ease: "linear" }}
              style={{ overflow: "hidden", whiteSpace: "nowrap" }}
            >
              Next Quest <span className="text-sandy-brown">Playlist</span>
            </motion.h1>
          </div>
          <Search />
          <LikedSongs />
        </div>
      </QueryClientProvider>
    );
  }
}

export default App;
