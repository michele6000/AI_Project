import { Directive, ViewContainerRef } from '@angular/core';

@Directive({
  selector: '[appGroupHost]',
})
export class GroupDirective {
  constructor(public viewContainerRef: ViewContainerRef) { }
}
