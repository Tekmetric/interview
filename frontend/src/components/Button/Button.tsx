import React, { ButtonHTMLAttributes } from "react";

const Button = (props: ButtonHTMLAttributes<HTMLButtonElement>) => (
  <button
    {...props}
    className="px-4 py-1 text-sm text-slate-50 rounded-full border bg-blue-600 hover:bg-blue-800 disabled:opacity-75 disabled:cursor-not-allowed"
  />
);

export default Button;
