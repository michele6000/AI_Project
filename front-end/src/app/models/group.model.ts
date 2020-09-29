import {VmModel} from './vm.model';

// tslint:disable:variable-name
export class GroupModel {
  id: number;
  name: string;
  status: number;
  courseName: string;
  members: any[];
  vms: VmModel[];
  proposer: string;


  limit_hdd: number;
  limit_cpu: number;
  limit_ram: number;
  limit_instance: number;
  limit_active_instance: number;
}
