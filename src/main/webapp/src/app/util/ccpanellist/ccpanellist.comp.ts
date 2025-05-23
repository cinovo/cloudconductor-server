import { Component, Input, Output, EventEmitter } from '@angular/core';

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
@Component({
    selector: 'cc-panel-list',
    templateUrl: './ccpanellist.comp.html',
    standalone: false
})
export class CCPanelListComponent {

  @Input() title: string;
  @Input() type = 'element';
  @Input() icon: string;
  @Input() elements: string[];
  @Input() allElements: string[];
  @Input() showLinks = true;

  @Output() onRemove: EventEmitter<string> = new EventEmitter();
  @Output() onAdd: EventEmitter<string> = new EventEmitter();

  public showNewElement = false;
  public newElement = '';
  public selectableElements: string[];

  constructor() {  }

  public goToNewElement(): void {
    this.newElement = '';
    this.showNewElement = true;
    this.selectableElements = this.allElements.filter(e => !this.elements.includes(e));
  }

  public addElement(newElement: string): void {
    this.onAdd.emit(newElement);
    this.newElement = '';
    this.showNewElement = false;
  }

  public removeElement(elementToRemove: string): void {
    this.onRemove.emit(elementToRemove);
  }

}
