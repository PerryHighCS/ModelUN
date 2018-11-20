# ModelUN
A simulation of the iterated Peace War game with multiple simultaneous relationships


## The General Assembly

Teams will be responsible for creating a Delegate, the ambassador to be sent to the Model UntiedNations. A delegate to the Untied Nations General Assembly needs to be able perform four operations. The country must be able to declare its name, record the wealth of all nations, declare war, receive other countries declarations of war, and handle correspondence with other delegates.

The first meeting of the General Assembly starts with a roll call. The delegates will each declare the name of the country they represent. After the roll call, the current wealth of each country will be tallied and reported to the delegates. Any correspondence from the delegates is collected. Next, a silent, binding poll will be taken in which each country will record their declarations of war with each other country. Once the poll is taken, wars will be announced. The meeting will then end.

After all wars are fought, the resource distribution will be tallied. Correspondence from the previous meeting will be delivered and another meeting will be called.

## Diplomacy

The rules of diplomacy in the Model Untied Nations are simple. Each country has a diplomatic relationship with each other country. For now, we will imagine that the relationship between 2 countries is distinct, having no dependancy on the relationships between any other countries (no alliances). This will help explain how wealth is accumulated.

In a peaceful cycle, a country will develop 2 resources due to internal manufacturing and trade with another other country. If the country goes to war with another country, the resources the first country would develop through trade with the other country will be lost. For that cycle, the country will only develop 1 resource, arming up for the war. If the other country, however, does not simultaneously declare war, the belligerent nation will invade and steal the 2 resources the pacifist develops during the time. If both nations were to declare war, the borders would remain secure, but only 1 resource would be earned by each.

A table of the resources earned in one cycle based on the actions between two delegates **A** and _B_:

<table>
  <tr><td>( <b>A</b>, <i>B</i> )</td><td> <i>Peace</i> </td><td> <i>Attack</i> </td> </tr>  
  <tr><td><b>Peace</b></td><td>(<b>2</b>, <i>2</i>)</td><td>(<b>0</b>, <i>3</i>)</td></tr>
  <tr><td><b>Attack</b></td><td>(<b>3</b>, <i>0</i>)</td><td>(<b>1</b>, <i>1</i>)</td></tr>
</table>
