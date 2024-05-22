import React, { ComponentProps } from "react";
import { Link as RouterLink } from "react-router-dom";

const Link = (props: Omit<ComponentProps<typeof RouterLink>, 'className'>) => (
  <RouterLink
    className="text-md text-blue-600 visited:text-purple-600"
    {...props}
  />
);

export default Link;
