import React from "react";
import Link from "../../components/Link/Link";

const About = () => (
  <>
    <h4 className="text-md font-medium text-slate-700 pt-2">
      This page is the about page; you'll find some details about the project.
    </h4>
    <section className="mt-3 text-sm font-medium text-slate-700">
      <span>
        While implementing this project I've touched the following topics:
      </span>
      <h5 className="mt-2">Project Related Topics</h5>
      <ol className="list-decimal list-inside ">
        <li>Introducing typescript</li>
        <li>Using a data fetch library (use-http)</li>
        <li>Adding a basic form to get query parameters</li>
        <li>Adding a basic listing to display results</li>
        <li>Adding a basic pagination to limit results</li>
        <li>Introducing tailwindcss for styling</li>
        <li>Introducing routing</li>
      </ol>
      <h5 className="mt-2">Other Topics</h5>
      <ol className="list-decimal list-inside ">
        <li>Upgrading React to v18</li>
        <li>Upgrading react-scripts to v5.x</li>
      </ol>
    </section>
    <Link to="/">Go to Query Screen</Link>
  </>
);

export default About;
