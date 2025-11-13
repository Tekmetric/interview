import React, { useState } from "react";
import styled from "styled-components";

const SearchForm = styled.form`
  margin-bottom: ${(props) => props.theme.spacing.xl};
`;

const SearchInput = styled.input`
  padding: ${(props) => props.theme.spacing.sm};
  margin-right: 10px;
  width: 250px;
  border: 1px solid #ccc;
  border-radius: ${(props) => props.theme.spacing.xs};
  font-size: ${(props) => props.theme.fontSizes.base};

  &:disabled {
    background-color: ${(props) => props.theme.colors.lightGray};
    cursor: not-allowed;
  }
`;

const Button = styled.button`
  padding: ${(props) => props.theme.spacing.sm}
    ${(props) => props.theme.spacing.md};
  margin-left: ${(props) => props.marginLeft || "0"};
  background-color: ${(props) => props.theme.colors.primary};
  color: ${(props) => props.theme.colors.white};
  border: none;
  border-radius: ${(props) => props.theme.spacing.xs};
  cursor: pointer;
  font-size: ${(props) => props.theme.fontSizes.base};

  &:hover {
    background-color: #555;
  }

  &:disabled {
    background-color: ${(props) => props.theme.colors.lightGray};
    color: ${(props) => props.theme.colors.secondary};
    cursor: not-allowed;
  }
`;

const SearchBar = ({ onSearch, disabled }) => {
  const [searchTerm, setSearchTerm] = useState("");
  return (
    <SearchForm
      onSubmit={(e) => {
        e.preventDefault();
      }}
    >
      <SearchInput
        type="text"
        placeholder={
          disabled ? "Select a set first..." : "Search cards in selected set..."
        }
        value={searchTerm}
        onChange={(e) => {
          e.preventDefault();
          setSearchTerm(e.target.value);
        }}
        disabled={disabled}
      />
      <Button
        type="submit"
        disabled={disabled}
        onClick={() => {
          onSearch(searchTerm);
        }}
      >
        Search
      </Button>
      {searchTerm && (
        <Button
          type="button"
          onClick={() => {
            setSearchTerm("");
            onSearch("");
          }}
          marginLeft="10px"
        >
          Clear
        </Button>
      )}
    </SearchForm>
  );
};

export default SearchBar;
