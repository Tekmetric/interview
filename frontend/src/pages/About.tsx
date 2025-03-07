import {
    Typography,
    TableContainer,
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableRow,
    Paper} from "@mui/material";
import React from "react";

const About: React.FC = () => {
  return (
    <div>
      <div className="flex justify-between items-center mb-4">
        <Typography variant="h4" className="font-bold">
          About
        </Typography>
      </div>

      <p className="text-gray-700 mb-6">
        This project is a full-stack application that was developed as a take-home for a Full Stack Software Engineer position at Tekmetric.
      </p>

      <p className="text-gray-700 mb-6">
        It allows users to manage reading lists.
        It provides role-based access, allowing different levels of interaction with books,
        authors, and reading lists.
      </p>

      <h2 className="text-2xl font-semibold mt-4 mb-2">Frontend</h2>
      <p className="text-gray-700">
        Built with React, React Router, TypeScript, Material UI, Tailwind, and Zod.
        The frontend offers:
      </p>
      <ul className="list-disc pl-5 text-gray-700 mb-4">
        <li>Read-only access to shared reading lists</li>
        <li>Authentication & authorization with role-based access</li>
        <li>Pagination, filtering, and sorting</li>
        <li>Forms with validation and custom hooks</li>
        <li>Context-based state management</li>
      </ul>

      <h2 className="text-2xl font-semibold mt-4 mb-2">Backend</h2>
      <p className="text-gray-700">
        Developed with Spring Boot, Spring Security, Spring Data JPA, and H2 Database,
        using Lombok, Spring Validation, and Maven.
      </p>
      <ul className="list-disc pl-5 text-gray-700 mb-4">
        <li>JWT-based authentication & authorization</li>
        <li>Public and restricted API endpoints</li>
        <li>Connection pooling</li>
        <li>Pagination</li>
        <li>Input validation</li>
      </ul>

      <h2 className="text-2xl font-semibold mt-4 mb-2">Built-in Users</h2>

      <p className="text-2x1">You can explore the interface using the following username-password pairs, or by registering your self.</p>

      <TableContainer component={Paper} sx={{ width: "auto", maxWidth: "600px", margin: "0" }} className="mt-6">
        <Table>
            <TableHead>
                <TableRow>
                    <TableCell><Typography variant="h5" className="font-bold">User Type</Typography></TableCell>
                    <TableCell><Typography variant="h5" className="font-bold">Username</Typography></TableCell>
                    <TableCell><Typography variant="h5" className="font-bold">Password</Typography></TableCell>
                </TableRow>
            </TableHead>
            <TableBody>
                <TableRow key={1}>
                    <TableCell><Typography variant="caption" className="font-bold">ADMIN USER</Typography></TableCell>
                    <TableCell><Typography variant="caption" className="font-bold">admin@email.com</Typography></TableCell>
                    <TableCell><Typography variant="caption" className="font-bold">123</Typography></TableCell>
                </TableRow>
                <TableRow key={1}>
                    <TableCell><Typography variant="caption" className="font-bold">NORMAL USER</Typography></TableCell>
                    <TableCell><Typography variant="caption" className="font-bold">user@email.com</Typography></TableCell>
                    <TableCell><Typography variant="caption" className="font-bold">123</Typography></TableCell>
                </TableRow>
            </TableBody>
        </Table>
      </TableContainer>
    </div>
  );
};

export default About;
