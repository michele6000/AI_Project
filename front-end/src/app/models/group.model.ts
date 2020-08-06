import {StudentModel} from './student.model';
import {VmModel} from './vm.model';

export class GroupModel {
  id: number;
  name: string;
  status: number;
  courseName: string;
  members: StudentModel[];
  vms: VmModel[];
}
