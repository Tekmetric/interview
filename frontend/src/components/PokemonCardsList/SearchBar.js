import React, { useState } from "react";
import styled from "styled-components";
import Button from "./Button";

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
          style={{ marginLeft: "10px" }}
        >
          Clear
        </Button>
      )}
    </SearchForm>
  );
};

export default SearchBar;
