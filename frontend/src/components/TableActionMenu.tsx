import { DeleteIcon, EditIcon, SettingsIcon } from "@chakra-ui/icons";
import { Menu, MenuButton, IconButton, MenuList, MenuItem } from "@chakra-ui/react";
import { TeeTime } from "../interfaces/tee-time.interface";

export interface TableActionMenuProps {
  row: TeeTime;
  handleDelete: (row: TeeTime) => void;
  handleEdit: (row: TeeTime) => void;
}

export default function TableActionMenu({ row, handleDelete: onDelete, handleEdit: onEdit }: TableActionMenuProps) {

  return (
    <>
    <Menu >
      <MenuButton
        as={IconButton}
        className="!bg-yellow-400"
        aria-label='Actions'
        icon={<SettingsIcon />}
        variant={'outline'}
      />
      <MenuList className="!text-black">
        <MenuItem onClick={() => onEdit(row)} icon={<EditIcon />}>
          Edit
        </MenuItem>
        <MenuItem onClick={() => onDelete(row)} icon={<DeleteIcon />}>
          Delete
        </MenuItem>
      </MenuList>
    </Menu>
    </>
  )
}