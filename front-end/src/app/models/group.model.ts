import {VmModel} from './vm.model';

export class GroupModel {
  id: number;
  name: string;
  vcpu: number;
  ram: number;
  disk: number;
  maxActiveVmSimultaneously: number;
  maxVm: number;
  vms?: VmModel[];
}
