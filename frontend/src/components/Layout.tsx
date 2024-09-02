import { BoxProps, Box } from "@mui/material";
import React from "react";

export function Root(props: BoxProps) {
  return (
    <Box
      {...props}
      sx={[
        {
          display: 'flex',
          flexDirection: 'column',
          minHeight: '100vh'
        },
        ...(Array.isArray(props.sx) ? props.sx : [props.sx])
      ]}
    />
  );
}

export function Header(props: BoxProps) {
  return (
    <Box
      component="header"
      className="header"
      {...props}
      sx={[
        {
          display: 'flex',
          flexDirection: 'row',
          position: 'sticky',
          top: 0,
          zIndex: 1100,
          backgroundColor: 'white'
        },
        ...(Array.isArray(props.sx) ? props.sx : [props.sx])
      ]}
    />
  );
}

export function Main(props: BoxProps) {
  return (
    <Box
      component="main"
      className="Main"
      {...props}
      sx={[{ overflow: 'auto', flex: '1' }, ...(Array.isArray(props.sx) ? props.sx : [props.sx])]}
    />
  );
}

export function MainContent(props: BoxProps) {
  return (
    <Box
      className="MainContent"
      {...props}
      sx={[{ overflow: 'auto', flex: '1', padding: 2 }, ...(Array.isArray(props.sx) ? props.sx : [props.sx])]}
    />
  );
}
