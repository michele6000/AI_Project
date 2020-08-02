import {StudentModel} from './student.model';

export class GroupModel {
  id: number;
  name: string;
  status: number;
  courseName: string;
  members: StudentModel[];
}
